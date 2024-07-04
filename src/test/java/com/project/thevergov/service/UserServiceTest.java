package com.project.thevergov.service;


import com.project.thevergov.cache.CacheStore;
import com.project.thevergov.dto.User;
import com.project.thevergov.entity.ConfirmationEntity;
import com.project.thevergov.entity.CredentialEntity;
import com.project.thevergov.entity.RoleEntity;
import com.project.thevergov.entity.UserEntity;
import com.project.thevergov.enumeration.Authority;
import com.project.thevergov.enumeration.LoginType;
import com.project.thevergov.event.UserEvent;
import com.project.thevergov.exception.ApiException;
import com.project.thevergov.repository.ConfirmationRepository;
import com.project.thevergov.repository.CredentialRepository;
import com.project.thevergov.repository.RoleRepository;
import com.project.thevergov.repository.UserRepository;
import com.project.thevergov.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private CredentialRepository credentialRepository;
    @Mock
    private ConfirmationRepository confirmationRepository;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private CacheStore<String, Integer> userCache;

    @InjectMocks
    private UserServiceImpl userServiceImpl;


    @Test
    @DisplayName("Test Find user by ID")
    public void getUserByUserIdTest() {

        // Arrange - Given
        var userEntity = new UserEntity();

        userEntity.setFirstName("Junior");
        userEntity.setId(1L);
        userEntity.setUserId("1");
        userEntity.setCreatedAt(LocalDateTime.of(1990, 11, 1, 1, 11, 11));
        userEntity.setUpdatedAt(LocalDateTime.of(1990, 11, 1, 1, 11, 11));
        userEntity.setLastLogin(LocalDateTime.of(1990, 11, 1, 1, 11, 11));

        var roleEntity = new RoleEntity("USER", Authority.USER);
        userEntity.setRole(roleEntity);

        var credentialEntity = new CredentialEntity();
        credentialEntity.setUpdatedAt(LocalDateTime.of(1990, 11, 1, 1, 11, 11));
        credentialEntity.setPassword("password");
        credentialEntity.setUserEntity(userEntity);

        when(userRepository.findUserByUserId("1")).thenReturn(Optional.of(userEntity));
        when(credentialRepository.getCredentialByUserEntityId(1L)).thenReturn(Optional.of(credentialEntity));

        // Act - When
        var userByUserId = userServiceImpl.getUserByUserId("1");

        // Assert - Then
        assertThat(userByUserId.getFirstName()).isEqualTo(userEntity.getFirstName());
        assertThat(userByUserId.getUserId()).isEqualTo("1");

    }

    @Test
    @DisplayName("Test Create User - Successful Creation")
    public void createUserTest() {
        // Arrange - Given
        String firstName = "New";
        String lastName = "User";
        String username = "newuser";
        String email = "newuser@example.com";
        String password = "password123";

        RoleEntity role = new RoleEntity("USER", Authority.USER);
        when(roleRepository.findRoleEntityByName(Authority.USER.name())).thenReturn(Optional.of(role));

        // Mock the saving behavior of repositories (if needed)
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            user.setId(1L); // Simulate ID assignment by the database
            return user;
        });

        // Act - When
        userServiceImpl.createUser(firstName, lastName, username, email, password);

        // Assert - Then

        // 1. Verify user creation and password storage
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userCaptor.capture());
        UserEntity savedUser = userCaptor.getValue();
        assertThat(savedUser.getFirstName()).isEqualTo(firstName);
        assertThat(savedUser.getLastName()).isEqualTo(lastName);
        assertThat(savedUser.getUsername()).isEqualTo(username);
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getRole()).isEqualTo(role);

        ArgumentCaptor<CredentialEntity> credentialCaptor = ArgumentCaptor.forClass(CredentialEntity.class);
        verify(credentialRepository).save(credentialCaptor.capture());
        CredentialEntity savedCredential = credentialCaptor.getValue();
        assertThat(savedCredential.getUserEntity()).isEqualTo(savedUser);
        // You might want to verify the password hashing here if you're using BCryptPasswordEncoder

        // 2. Verify confirmation entity creation
        ArgumentCaptor<ConfirmationEntity> confirmationCaptor = ArgumentCaptor.forClass(ConfirmationEntity.class);
        verify(confirmationRepository).save(confirmationCaptor.capture());
        ConfirmationEntity savedConfirmation = confirmationCaptor.getValue();
        assertThat(savedConfirmation.getUserEntity()).isEqualTo(savedUser);


    }

//    @Test
//    @DisplayName("Test Verify Account")
//    public void verifyAccountTest() {
//        // Arrange - Given
//        String key = "verification_key";
//        UserEntity userEntity = new UserEntity();
//        userEntity.setEmail("test@example.com"); // Set email
//        userEntity.setEnabled(false);
//
//        ConfirmationEntity confirmationEntity = new ConfirmationEntity(userEntity);
//        confirmationEntity.setTokenKey(key);
//        when(confirmationRepository.findByTokenKey(key)).thenReturn(Optional.of(confirmationEntity));
//        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(userEntity));
//
//        // Act - When
//        userServiceImpl.verifyAccount(key);
//
//        // Assert - Then
//        assertThat(userEntity.isEnabled()).isTrue();
//        verify(userRepository).save(userEntity);
//        verify(confirmationRepository).delete(confirmationEntity);
//    }

    @Test
    @DisplayName("Test Get User By Email - Successful Retrieval")
    public void getUserByEmailTest() {
        // Arrange - Given
        String email = "john.doe@example.com";
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("John");
        userEntity.setLastName("Doe");
        userEntity.setEmail(email);
        userEntity.setUsername("johndoe");
        userEntity.setCreatedAt(LocalDateTime.of(1990, 11, 1, 1, 11, 11));
        userEntity.setUpdatedAt(LocalDateTime.of(1990, 11, 1, 1, 11, 11));
        userEntity.setLastLogin(LocalDateTime.of(1990, 11, 1, 1, 11, 11));

        RoleEntity userRole = new RoleEntity("USER", Authority.USER);
        userEntity.setRole(userRole);

        CredentialEntity credentialEntity = new CredentialEntity();
        credentialEntity.setUserEntity(userEntity);
        credentialEntity.setUpdatedAt(LocalDateTime.of(1990, 11, 1, 1, 11, 11));
        credentialEntity.setPassword("hashedPassword"); // Assuming hashed password storage

        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(userEntity));
        when(credentialRepository.getCredentialByUserEntityId(userEntity.getId())).thenReturn(Optional.of(credentialEntity));

        // Act - When
        User resultUser = userServiceImpl.getUserByEmail(email);

        // Assert - Then
        assertThat(resultUser).isNotNull();
        assertThat(resultUser.getFirstName()).isEqualTo(userEntity.getFirstName());
        assertThat(resultUser.getLastName()).isEqualTo(userEntity.getLastName());
        assertThat(resultUser.getEmail()).isEqualTo(userEntity.getEmail());
        assertThat(resultUser.getRole()).isEqualTo(userRole.getName()); // Assuming you want the role name in the User DTO
        // Optionally, assert other properties of the User DTO if they are relevant
    }

    @Test
    @DisplayName("Test Get User By Email - User Not Found")
    public void getUserByEmailNotFoundTest() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty()); // Mock user repository to return empty Optional

        // Act & Assert
        assertThatThrownBy(() -> userServiceImpl.getUserByEmail(email))
                .isInstanceOf(ApiException.class)
                .hasMessage("Confirmation key not found"); // This is the expected exception message
    }

    @Test
    @DisplayName("Test Get User Credential By ID - Successful Retrieval")
    public void getUserCredentialByIdTest() {
        // Arrange - Given
        Long userId = 1L;
        CredentialEntity credentialEntity = new CredentialEntity();
        credentialEntity.setId(10L); // Sample ID for the credential
        credentialEntity.setUserEntity(new UserEntity()); // You can set the user entity if needed
        credentialEntity.setPassword("hashedPassword");

        when(credentialRepository.getCredentialByUserEntityId(userId)).thenReturn(Optional.of(credentialEntity));

        // Act - When
        CredentialEntity resultCredential = userServiceImpl.getUserCredentialById(userId);

        // Assert - Then
        assertThat(resultCredential).isEqualTo(credentialEntity);
        // Optionally, assert individual properties of the credential if necessary
    }


    @Test
    @DisplayName("Test Get User Credential By ID - Credential Not Found")
    public void getUserCredentialByIdNotFoundTest() {
        // Arrange
        Long userId = 1L;
        when(credentialRepository.getCredentialByUserEntityId(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userServiceImpl.getUserCredentialById(userId))
                .isInstanceOf(ApiException.class)
                .hasMessage("Unable to find user credential");
    }

    @Test
    @DisplayName("Test Verify Account - Successful Verification")
    public void verifyAccountTest() {
        // Arrange - Given
        String verificationKey = "valid_key";
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("test@example.com");
        userEntity.setEnabled(false); // Initially disabled

        ConfirmationEntity confirmationEntity = new ConfirmationEntity();
        confirmationEntity.setTokenKey(verificationKey);
        confirmationEntity.setUserEntity(userEntity);

        // Mock repository behaviors
        when(confirmationRepository.findByTokenKey(verificationKey)).thenReturn(Optional.of(confirmationEntity));
        when(userRepository.findByEmailIgnoreCase(userEntity.getEmail())).thenReturn(Optional.of(userEntity)); // Mock the user lookup

        // Act - When
        userServiceImpl.verifyAccount(verificationKey);

        // Assert - Then

        // 1. Verify user enabled
        assertThat(userEntity.isEnabled()).isTrue();

        // 2. Verify repository calls
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userCaptor.capture()); // Verify that the user was saved
        assertThat(userCaptor.getValue()).isEqualTo(userEntity);

        verify(confirmationRepository).delete(confirmationEntity); // Verify that the confirmation entity was deleted
    }

    @Test
    @DisplayName("Test Verify Account - Invalid Key")
    public void verifyAccountInvalidKeyTest() {
        // Arrange - Given
        String invalidKey = "invalid_key";

        // Mock repository behavior
        when(confirmationRepository.findByTokenKey(invalidKey)).thenReturn(Optional.empty()); // No confirmation found

        // Act & Assert
        assertThatThrownBy(() -> userServiceImpl.verifyAccount(invalidKey))
                .isInstanceOf(NullPointerException.class); // Or handle the exception as per your design
    }

    @Test
    @DisplayName("Test Update Login Attempt - Login Attempt")
    public void updateLoginAttemptTest_LoginAttempt() {
        // Arrange
        String email = "test@example.com";
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(email);
        userEntity.setLoginAttempts(0);
        userEntity.setAccountNonLocked(true);

        // Mock repository and cache behavior
        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(userEntity));
        when(userCache.get(email)).thenReturn(null); // Simulate cache miss for the first attempt

        // Act
        userServiceImpl.updateLoginAttempt(email, LoginType.LOGIN_ATTEMPT); // First attempt

        // Assert
        assertThat(userEntity.getLoginAttempts()).isEqualTo(1);
        assertThat(userEntity.isAccountNonLocked()).isTrue();
        verify(userCache).put(email, 1);

        // Simulate multiple login attempts
        for (int i = 1; i < 6; i++) {
            when(userCache.get(email)).thenReturn(i); // Simulate increasing attempts in the cache
            userServiceImpl.updateLoginAttempt(email, LoginType.LOGIN_ATTEMPT);
        }

        assertThat(userEntity.getLoginAttempts()).isEqualTo(6);
        assertThat(userEntity.isAccountNonLocked()).isFalse(); // Account locked after 5 attempts
        verify(userCache).put(email, 6);
    }

    @Test
    @DisplayName("Test Update Login Attempt - Login Success")
    public void updateLoginAttemptTest_LoginSuccess() {
        // Arrange
        String email = "test@example.com";
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(email);
        userEntity.setLoginAttempts(3);
        userEntity.setAccountNonLocked(false); // Start with locked account

        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(userEntity));

        // Act
        userServiceImpl.updateLoginAttempt(email, LoginType.LOGIN_SUCCESS);

        // Assert
        assertThat(userEntity.getLoginAttempts()).isEqualTo(0);
        assertThat(userEntity.isAccountNonLocked()).isTrue();
        verify(userCache).evict(email);

        // Verify last login time update
        LocalDateTime now = LocalDateTime.now();
        assertThat(userEntity.getLastLogin()).isCloseTo(now, within(1, ChronoUnit.SECONDS));
    }


}
