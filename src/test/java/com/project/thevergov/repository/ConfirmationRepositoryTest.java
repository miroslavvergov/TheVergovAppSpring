package com.project.thevergov.repository;

import com.project.thevergov.entity.ConfirmationEntity;
import com.project.thevergov.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ConfirmationRepositoryTest {

    @Autowired
    private ConfirmationRepository confirmationRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity testUser;
    private ConfirmationEntity testConfirmation;

    @BeforeEach
    public void setUp() {

    }

}
