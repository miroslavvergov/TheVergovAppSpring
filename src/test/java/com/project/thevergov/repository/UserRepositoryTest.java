package com.project.thevergov.repository;

import com.project.thevergov.domain.RequestContext;
import com.project.thevergov.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UserRepositoryTest {

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
    private UserRepository userRepository;

    private UserEntity testUser;

    @BeforeEach
    public void setUp() {
        // Set a user ID in the RequestContext
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
    }

    @Test
    public void whenFindByEmailIgnoreCase_thenReturnUser() {
        Optional<UserEntity> found = userRepository.findByEmailIgnoreCase(testUser.getEmail());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    public void whenFindByEmailIgnoreCaseWithDifferentCase_thenReturnUser() {
        Optional<UserEntity> found = userRepository.findByEmailIgnoreCase("JOHNDOE@EXAMPLE.COM");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    public void whenFindByEmailIgnoreCaseWithNonExistentEmail_thenReturnEmpty() {
        Optional<UserEntity> found = userRepository.findByEmailIgnoreCase("nonexistent@example.com");

        assertThat(found).isNotPresent();
    }

    @Test
    public void whenFindByUserId_thenReturnUser() {
        Optional<UserEntity> found = userRepository.findUserByUserId(testUser.getUserId());

        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(testUser.getUserId());
    }

    @Test
    public void whenFindByUserIdWithNonExistentUserId_thenReturnEmpty() {
        Optional<UserEntity> found = userRepository.findUserByUserId("nonexistentUserId");

        assertThat(found).isNotPresent();
    }

    @Test
    public void whenSave_thenReturnSavedUser() {
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

        UserEntity savedUser = userRepository.save(newUser);

        Optional<UserEntity> found = userRepository.findById(savedUser.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(newUser.getUserId());
    }
}
