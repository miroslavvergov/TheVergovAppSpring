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
import com.project.thevergov.utils.UserUtils;
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
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import static com.project.thevergov.constant.Constants.NINETY_DAYS;
import static com.project.thevergov.constant.Constants.PHOTO_STORAGE_PATH;
import static com.project.thevergov.utils.UserUtils.*;
import static com.project.thevergov.validation.UserValidation.verifyAccountStatus;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.logging.log4j.util.Strings.EMPTY;


/**
 * Service implementation for managing users.
 * <p>
 * This class provides implementations for the methods defined in the UserService
 * interface. It interacts with the UserRepository to perform CRUD operations and
 * uses transactions to ensure data integrity.
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final CredentialRepository credentialRepository;

    private final ConfirmationRepository confirmationRepository;

    private final BCryptPasswordEncoder encoder;

    private final CacheStore<String, Integer> userCache;

    private final ApplicationEventPublisher publisher;

    private final ModelMapper modelMapper;

    @Override
    public void createUser(String firstName, String lastName, String username, String email, String password) {

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
        var role = roleRepository.findRoleEntityByName(name);


        return role.orElseThrow(() -> new ApiException("Role not found"));
    }

    @Override
    public void verifyAccount(String key) {
        var confirmationEntity = getUserConfirmation(key);
        UserEntity userEntity = getUserEntityByEmail(confirmationEntity.getUserEntity().getEmail());
        userEntity.setEnabled(true);
        userRepository.save(userEntity);
        confirmationRepository.delete(confirmationEntity);
    }

    @Override
    public void updateLoginAttempt(String email, LoginType loginType) {
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
        var userEntity = userRepository.findUserByUserId(userId).orElseThrow(() -> new ApiException("User not found"));
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User getUserByEmail(String email) {
        UserEntity userEntity = getUserEntityByEmail(email);
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }


    @Override
    public CredentialEntity getUserCredentialById(Long userId) {
        var credentialById = credentialRepository.getCredentialByUserEntityId(userId);

        return credentialById.orElseThrow(() -> new ApiException("Unable to find user credential"));
    }

    @Override
    public User setupMfa(Long id) {
        UserEntity userEntity = getUserEntityById(id);
        var codeSecret = qrCodeSecret.get();
        userEntity.setQrCodeImageUri(qrCodeImageUri.apply(userEntity.getEmail(), codeSecret));
        userEntity.setQrCodeSecret(codeSecret);
        userEntity.setMfa(true);
        userRepository.save(userEntity);
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));

    }

    private UserEntity getUserEntityById(Long id) {
        var userById = userRepository.findById(id);
        return userById.orElseThrow(() -> new ApiException("User not found"));
    }

    @Override
    public User cancelMfa(Long id) {
        UserEntity userEntity = getUserEntityById(id);
        userEntity.setMfa(false);
        userEntity.setQrCodeSecret(EMPTY);
        userEntity.setQrCodeImageUri(EMPTY);
        userRepository.save(userEntity);
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User verifyQrCode(String userId, String qrCode) {
        UserEntity userEntity = getUserEntityByUserId(userId);
        verifyCode(qrCode, userEntity.getQrCodeSecret());
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public void resetPassword(String email) {

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
        var confirmationEntity = getUserConfirmation(key);
        if (confirmationEntity == null) {
            //TODO
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
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setRole(getRoleName(role));
        userRepository.save(userEntity);
    }

    @Override
    public void toggleAccountExpired(String userId) {
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setAccountNonExpired(!userEntity.isAccountNonExpired());
        userRepository.save(userEntity);
    }

    @Override
    public void toggleAccountLocked(String userId) {
        var userEntity = getUserEntityByUserId(userId);

        userEntity.setAccountNonLocked(!userEntity.isAccountNonLocked());
        userRepository.save(userEntity);
    }

    @Override
    public void toggleAccountEnabled(String userId) {
        var userEntity = getUserEntityByUserId(userId);

        userEntity.setEnabled(!userEntity.isEnabled());
        userRepository.save(userEntity);
    }

    @Override
    public void toggleCredentialsExpired(String userId) {
        var userEntity = getUserEntityByUserId(userId);
        var credentials = getUserCredentialById(userEntity.getId());
        credentials.setUpdatedAt(LocalDateTime.of(1995, 7, 12, 11, 11));
//        if (credentials.getUpdatedAt().plusDays(NINETY_DAYS).isAfter(LocalDateTime.now())) {
//            credentials.setUpdatedAt(LocalDateTime.of(1995, 7, 12, 11, 11));
//        } else {
//            credentials.setUpdatedAt(LocalDateTime.now());
//        }
        userRepository.save(userEntity);
    }

    @Override
    public void updatePassword(String userId, String password, String newPassword, String confirmNewPassword) {
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
        var user = getUserEntityByUserId(userId);
        var photoUrl = photoFunction.apply(userId, file);
        user.setImageUrl(photoUrl + "timestamp=" + System.currentTimeMillis());
        userRepository.save(user);
        return photoUrl;
    }

    private final BiFunction<String, MultipartFile, String> photoFunction = (id, file) -> {
        var fileName = id + ".png";
        try {
            var fileStorageLocation = Paths.get(PHOTO_STORAGE_PATH).toAbsolutePath().normalize();
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
        var userByUserId = userRepository.findUserByUserId(userId);
        return userByUserId.orElseThrow(() -> new ApiException("User not found"));
    }

    private ConfirmationEntity getUserConfirmation(UserEntity user) {
        return confirmationRepository.findByUserEntity(user).orElse(null);
    }

    private boolean verifyCode(String qrCode, String qrCodeSecret) {
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
        var userByEmail = userRepository.findByEmailIgnoreCase(email);
        return userByEmail.orElseThrow(() -> new ApiException("Confirmation key not found"));
    }

    private ConfirmationEntity getUserConfirmation(String key) {
        return confirmationRepository.findByTokenKey(key).orElse(null);
    }

    private UserEntity createNewUser(String firstName, String lastName, String username, String email) {
        var role = getRoleName(Authority.USER.name());

        return createUserEntity(firstName, lastName, username, email, role);

    }
}


























