package com.project.thevergov.repository;

import com.project.thevergov.domain.RequestContext;
import com.project.thevergov.entity.ConfirmationEntity;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class ConfirmationRepositoryTest {

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
    private ConfirmationRepository confirmationRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity testUser;
    private ConfirmationEntity testConfirmation;

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

        testConfirmation = ConfirmationEntity.builder()
                .userEntity(testUser)
                .tokenKey(UUID.randomUUID().toString())
                .build();

        confirmationRepository.save(testConfirmation);
    }

    @Test
    public void whenFindByTokenKey_thenReturnConfirmation() {
        Optional<ConfirmationEntity> found = confirmationRepository.findByTokenKey(testConfirmation.getTokenKey());

        assertThat(found).isPresent();
        assertThat(found.get().getTokenKey()).isEqualTo(testConfirmation.getTokenKey());
    }

    @Test
    public void whenFindByTokenKeyWithNonExistentToken_thenReturnEmpty() {
        Optional<ConfirmationEntity> found = confirmationRepository.findByTokenKey("nonexistentTokenKey");

        assertThat(found).isNotPresent();
    }

    @Test
    public void whenFindByUserEntity_thenReturnConfirmation() {
        Optional<ConfirmationEntity> found = confirmationRepository.findByUserEntity(testUser);

        assertThat(found).isPresent();
        assertThat(found.get().getUserEntity().getUserId()).isEqualTo(testUser.getUserId());
    }

    @Test
    public void whenSave_thenReturnSavedConfirmation() {
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

        ConfirmationEntity newConfirmation = ConfirmationEntity.builder()
                .userEntity(newUser)
                .tokenKey(UUID.randomUUID().toString())
                .build();

        ConfirmationEntity savedConfirmation = confirmationRepository.save(newConfirmation);

        Optional<ConfirmationEntity> found = confirmationRepository.findById(savedConfirmation.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTokenKey()).isEqualTo(newConfirmation.getTokenKey());
    }
}
