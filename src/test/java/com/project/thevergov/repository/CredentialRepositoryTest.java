package com.project.thevergov.repository;

import com.project.thevergov.domain.RequestContext;
import com.project.thevergov.entity.CredentialEntity;
import com.project.thevergov.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class CredentialRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.2")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    static {
        postgreSQLContainer.start();
        System.setProperty("DB_URL", postgreSQLContainer.getJdbcUrl());
        System.setProperty("DB_USERNAME", postgreSQLContainer.getUsername());
        System.setProperty("DB_PASSWORD", postgreSQLContainer.getPassword());
    }

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity testUser;
    private CredentialEntity testCredential;

    @BeforeEach
    public void setUp() {
        RequestContext.setUserId(0L); // Assuming user ID is 0L for testing

        testUser = UserEntity.builder()
                .userId("testUserId")
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("johndoe@example.com")
                .loginAttempts(0)
                .lastLogin(LocalDateTime.now())
                .bio("Test bio")
                .imageUrl("http://example.com/image.jpg")
                .accountNonExpired(true)
                .accountNonLocked(true)
                .enabled(true)
                .mfa(false)
                .qrCodeSecret("secret")
                .qrCodeImageUri("http://example.com/qrcode.jpg")
                .build();

        userRepository.save(testUser);

        testCredential = CredentialEntity.builder()
                .userEntity(testUser)
                .password("hashed_password")
                .build();

        credentialRepository.save(testCredential);
    }

    @Test
    public void whenGetCredentialByUserEntityId_thenReturnCredential() {
        Optional<CredentialEntity> found = credentialRepository.getCredentialByUserEntityId(testUser.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUserEntity().getId()).isEqualTo(testUser.getId());
        assertThat(found.get().getPassword()).isEqualTo(testCredential.getPassword());
    }

    @Test
    public void whenGetCredentialByNonExistentUserEntityId_thenReturnEmpty() {
        Optional<CredentialEntity> found = credentialRepository.getCredentialByUserEntityId(999L);

        assertThat(found).isNotPresent();
    }

    @Test
    public void whenSave_thenReturnSavedCredential() {
        UserEntity newUser = UserEntity.builder()
                .userId("newUserId")
                .firstName("Jane")
                .lastName("Smith")
                .username("janesmith")
                .email("janesmith@example.com")
                .loginAttempts(0)
                .lastLogin(LocalDateTime.now())
                .bio("New bio")
                .imageUrl("http://example.com/newimage.jpg")
                .accountNonExpired(true)
                .accountNonLocked(true)
                .enabled(true)
                .mfa(false)
                .qrCodeSecret("newsecret")
                .qrCodeImageUri("http://example.com/newqrcode.jpg")
                .build();

        userRepository.save(newUser);

        CredentialEntity newCredential = CredentialEntity.builder()
                .userEntity(newUser)
                .password("new_hashed_password")
                .build();

        CredentialEntity savedCredential = credentialRepository.save(newCredential);

        Optional<CredentialEntity> found = credentialRepository.findById(savedCredential.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUserEntity().getId()).isEqualTo(newUser.getId());
        assertThat(found.get().getPassword()).isEqualTo(newCredential.getPassword());
    }
}
