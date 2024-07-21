package com.project.thevergov.service.impl;

import com.project.thevergov.cache.CacheStore;
import com.project.thevergov.domain.RequestContext;
import com.project.thevergov.dto.User;
import com.project.thevergov.entity.ConfirmationEntity;
import com.project.thevergov.entity.CredentialEntity;
import com.project.thevergov.entity.RoleEntity;
import com.project.thevergov.entity.UserEntity;
import com.project.thevergov.enumeration.Authority;
import com.project.thevergov.enumeration.EventType;
import com.project.thevergov.enumeration.LoginType;
import com.project.thevergov.event.UserEvent;
import com.project.thevergov.exception.ApiException;
import com.project.thevergov.repository.ConfirmationRepository;
import com.project.thevergov.repository.CredentialRepository;
import com.project.thevergov.repository.RoleRepository;
import com.project.thevergov.repository.UserRepository;
import com.project.thevergov.service.UserService;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static com.project.thevergov.constant.Constants.FILE_STORAGE;
import static com.project.thevergov.utils.UserUtils.*;
import static com.project.thevergov.validation.UserValidation.verifyAccountStatus;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.logging.log4j.util.Strings.EMPTY;

/**
 * Implementation of the UserService interface for managing user operations.
 * <p>
 * This service handles CRUD operations for users, authentication, and user-specific
 * functionality like password management and multi-factor authentication (MFA).
 * Transactions are used to ensure data integrity.
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository; // Repository for user data
    private final RoleRepository roleRepository; // Repository for role data
    private final CredentialRepository credentialRepository; // Repository for user credentials
    private final ConfirmationRepository confirmationRepository; // Repository for account confirmation data
    private final BCryptPasswordEncoder encoder; // Encoder for hashing passwords
    private final CacheStore<String, Integer> userCache; // Cache to store login attempt counts
    private final ApplicationEventPublisher publisher; // Publisher for user-related events

    @Override
    public void createUser(String firstName, String lastName, String username, String email, String password) {
        // Create and save a new user, set credentials and confirmation, and publish an event
        UserEntity newUser = createNewUser(firstName, lastName, username, email);
        var userEntity = userRepository.save(newUser);
        var credentialEntity = new CredentialEntity(userEntity, password);
        credentialRepository.save(credentialEntity);
        var confirmationEntity = new ConfirmationEntity(userEntity);
        confirmationRepository.save(confirmationEntity);
        publisher.publishEvent(new UserEvent(userEntity, EventType.REGISTRATION, Map.of("key", confirmationEntity.getTokenKey())));
    }

    @Override
    public RoleEntity getRoleName(String name) {
        // Retrieve a role by name or throw an exception if not found
        var role = roleRepository.findRoleEntityByName(name);
        return role.orElseThrow(() -> new ApiException("Role not found"));
    }

    @Override
    public void verifyAccount(String key) {
        // Verify account using confirmation key and enable user account
        var confirmationEntity = getUserConfirmation(key);
        UserEntity userEntity = getUserEntityByEmail(confirmationEntity.getUserEntity().getEmail());
        userEntity.setEnabled(true);
        userRepository.save(userEntity);
        confirmationRepository.delete(confirmationEntity);
    }

    @Override
    public void updateLoginAttempt(String email, LoginType loginType) {
        // Update login attempt count and account lock status based on login type
        var userEntity = getUserEntityByEmail(email);
        RequestContext.setUserId(userEntity.getId());
        switch (loginType) {
            case LOGIN_ATTEMPT -> {
                if (userCache.get(userEntity.getEmail()) == null) {
                    userEntity.setLoginAttempts(0);
                    userEntity.setAccountNonLocked(true);
                }
                userEntity.setLoginAttempts(userEntity.getLoginAttempts() + 1);
                userCache.put(userEntity.getEmail(), userEntity.getLoginAttempts());
                if (userCache.get(userEntity.getEmail()) > 5) {
                    userEntity.setAccountNonLocked(false);
                }
            }
            case LOGIN_SUCCESS -> {
                userEntity.setAccountNonLocked(true);
                userEntity.setLoginAttempts(0);
                userEntity.setLastLogin(LocalDateTime.now());
                userCache.evict(userEntity.getEmail());
            }
        }
        userRepository.save(userEntity);
    }

    @Override
    public User getUserByUserId(String userId) {
        // Get a user by ID and convert it to a DTO
        var userEntity = userRepository.findUserByUserId(userId).orElseThrow(() -> new ApiException("User not found"));
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User getUserByEmail(String email) {
        // Get a user by email and convert it to a DTO
        UserEntity userEntity = getUserEntityByEmail(email);
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public CredentialEntity getUserCredentialById(Long userId) {
        // Retrieve user credentials by user ID
        var credentialById = credentialRepository.getCredentialByUserEntityId(userId);
        return credentialById.orElseThrow(() -> new ApiException("Unable to find user credential"));
    }

    @Override
    public User setupMfa(Long id) {
        // Set up multi-factor authentication (MFA) for a user
        UserEntity userEntity = getUserEntityById(id);
        var codeSecret = qrCodeSecret.get();
        userEntity.setQrCodeImageUri(qrCodeImageUri.apply(userEntity.getEmail(), codeSecret));
        userEntity.setQrCodeSecret(codeSecret);
        userEntity.setMfa(true);
        userRepository.save(userEntity);
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    private UserEntity getUserEntityById(Long id) {
        // Get a user entity by ID or throw an exception if not found
        var userById = userRepository.findById(id);
        return userById.orElseThrow(() -> new ApiException("User not found"));
    }

    @Override
    public User cancelMfa(Long id) {
        // Cancel MFA for a user and clear MFA-related data
        UserEntity userEntity = getUserEntityById(id);
        userEntity.setMfa(false);
        userEntity.setQrCodeSecret(EMPTY);
        userEntity.setQrCodeImageUri(EMPTY);
        userRepository.save(userEntity);
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User verifyQrCode(String userId, String qrCode) {
        // Verify the provided QR code for a user
        UserEntity userEntity = getUserEntityByUserId(userId);
        verifyCode(qrCode, userEntity.getQrCodeSecret());
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public void resetPassword(String email) {
        // Trigger password reset process for a user
        var user = getUserEntityByEmail(email);
        var confirmation = getUserConfirmation(user);
        if (confirmation != null) {
            publisher.publishEvent(new UserEvent(user, EventType.RESETPASSWORD, Map.of("key", confirmation.getTokenKey())));
        } else {
            var confirmationEntity = new ConfirmationEntity(user);
            confirmationRepository.save(confirmationEntity);
            publisher.publishEvent(new UserEvent(user, EventType.RESETPASSWORD, Map.of("key", confirmationEntity.getTokenKey())));
        }
    }

    @Override
    public User verifyPasswordKey(String key) {
        // Verify the password reset key and return the user if valid
        var confirmationEntity = getUserConfirmation(key);
        if (confirmationEntity == null) {
            throw new ApiException("Unable to find key");
        }
        var userEntity = getUserEntityByEmail(confirmationEntity.getUserEntity().getEmail());
        if (userEntity == null) {
            throw new ApiException("Incorrect token");
        }
        verifyAccountStatus(userEntity);
        confirmationRepository.delete(confirmationEntity);
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public void updatePassword(String userId, String newPassword, String confirmNewPassword) {
        // Update the user's password if the new passwords match
        if (!confirmNewPassword.equals(newPassword)) {
            throw new ApiException("Passwords don't match. Please try again.");
        }
        var user = getUserByUserId(userId);
        var credentials = getUserCredentialById(user.getId());
        credentials.setPassword(encoder.encode(newPassword));
        credentialRepository.save(credentials);
    }

    @Override
    public User updateUser(String userId, String firstName, String lastName, String email, String bio) {
        // Update user details and save changes
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);
        userEntity.setEmail(email);
        userEntity.setBio(bio);
        userRepository.save(userEntity);
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public void updateRole(String userId, String role) {
        // Update the user's role
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setRole(getRoleName(role));
        userRepository.save(userEntity);
    }

    @Override
    public void toggleAccountExpired(String userId) {
        // Toggle account expiration status
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setAccountNonExpired(!userEntity.isAccountNonExpired());
        userRepository.save(userEntity);
    }

    @Override
    public void toggleAccountLocked(String userId) {
        // Toggle account lock status
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setAccountNonLocked(!userEntity.isAccountNonLocked());
        userRepository.save(userEntity);
    }

    @Override
    public void toggleAccountEnabled(String userId) {
        // Toggle account enabled status
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setEnabled(!userEntity.isEnabled());
        userRepository.save(userEntity);
    }

    @Override
    public void toggleCredentialsExpired(String userId) {
        // Toggle credentials expiration status
        var userEntity = getUserEntityByUserId(userId);
        var credentials = getUserCredentialById(userEntity.getId());
        credentials.setUpdatedAt(LocalDateTime.of(1995, 7, 12, 11, 11)); // Hardcoded date for testing
        userRepository.save(userEntity);
    }

    @Override
    public void updatePassword(String userId, String password, String newPassword, String confirmNewPassword) {
        // Update the user's password after verifying the existing password
        if (!confirmNewPassword.equals(newPassword)) {
            throw new ApiException("Passwords don't match. Please try again.");
        }
        var user = getUserByUserId(userId);
        var credentials = getUserCredentialById(user.getId());
        if (!encoder.matches(password, credentials.getPassword())) {
            throw new ApiException("Existing password is incorrect. Please try again.");
        }
        credentials.setPassword(encoder.encode(newPassword));
        credentialRepository.save(credentials);
    }

    @Override
    public String uploadPhoto(String userId, MultipartFile file) {
        // Upload a user photo and return the URL
        var user = getUserEntityByUserId(userId);
        var photoUrl = photoFunction.apply(userId, file);
        user.setImageUrl(photoUrl + "timestamp=" + System.currentTimeMillis());
        userRepository.save(user);
        return photoUrl;
    }

    @Override
    public User getUserById(Long id) {
        // Get a user by ID and convert it to a DTO
        var userEntity = userRepository.findById(id).orElseThrow(() -> new ApiException("User not found"));
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public List<UserEntity> getUsers() {
        // TODO Retrieve all users
        return userRepository.findAll();
    }

    private final BiFunction<String, MultipartFile, String> photoFunction = (id, file) -> {
        // Function to handle photo upload and return the photo URL
        var fileName = id + ".png";
        try {
            var fileStorageLocation = Paths.get(FILE_STORAGE).toAbsolutePath().normalize();
            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }
            Files.copy(file.getInputStream(), fileStorageLocation.resolve(fileName), REPLACE_EXISTING);
            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/user/image/" + fileName).toUriString();
        } catch (Exception e) {
            throw new ApiException("unable to save image");
        }
    };

    private UserEntity getUserEntityByUserId(String userId) {
        // Get a user entity by user ID or throw an exception if not found
        var userByUserId = userRepository.findUserByUserId(userId);
        return userByUserId.orElseThrow(() -> new ApiException("User not found"));
    }

    private ConfirmationEntity getUserConfirmation(UserEntity user) {
        // Get confirmation entity associated with a user
        return confirmationRepository.findByUserEntity(user).orElse(null);
    }

    private boolean verifyCode(String qrCode, String qrCodeSecret) {
        // Verify the provided QR code against the stored secret
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        if (codeVerifier.isValidCode(qrCodeSecret, qrCode)) {
            return true;
        } else {
            throw new ApiException("Invalid QR code. Please try again.");
        }
    }

    private UserEntity getUserEntityByEmail(String email) {
        // Get a user entity by email or throw an exception if not found
        var userByEmail = userRepository.findByEmailIgnoreCase(email);
        return userByEmail.orElseThrow(() -> new ApiException("Confirmation key not found"));
    }

    private ConfirmationEntity getUserConfirmation(String key) {
        // Get confirmation entity by token key
        return confirmationRepository.findByTokenKey(key).orElse(null);
    }

    private UserEntity createNewUser(String firstName, String lastName, String username, String email) {
        // Create a new user entity with the specified details
        var role = getRoleName(Authority.USER.name());
        return createUserEntity(firstName, lastName, username, email, role);
    }
}
