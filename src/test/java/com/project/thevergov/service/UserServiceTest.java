package com.project.thevergov.service;


import com.project.thevergov.dto.User;
import com.project.thevergov.entity.ConfirmationEntity;
import com.project.thevergov.entity.CredentialEntity;
import com.project.thevergov.entity.RoleEntity;
import com.project.thevergov.entity.UserEntity;
import com.project.thevergov.enumeration.Authority;
import com.project.thevergov.event.UserEvent;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    @DisplayName("Test Verify Account")
    public void verifyAccountTest() {
        // Arrange - Given
        String key = "verification_key";
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("test@example.com"); // Set email
        userEntity.setEnabled(false);

        ConfirmationEntity confirmationEntity = new ConfirmationEntity(userEntity);
        confirmationEntity.setTokenKey(key);
        when(confirmationRepository.findByTokenKey(key)).thenReturn(Optional.of(confirmationEntity));
        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(userEntity));

        // Act - When
        userServiceImpl.verifyAccount(key);

        // Assert - Then
        assertThat(userEntity.isEnabled()).isTrue();
        verify(userRepository).save(userEntity);
        verify(confirmationRepository).delete(confirmationEntity);
    }
}
