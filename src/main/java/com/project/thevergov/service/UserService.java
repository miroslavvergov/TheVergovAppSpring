package com.project.thevergov.service;

import com.project.thevergov.domain.dto.SignupRequest;
import com.project.thevergov.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing users in the application.
 *
 * This interface defines common operations for user management such as saving,
 * retrieving, and deleting users. It is implemented by a class that handles
 * the business logic for these operations.
 */
public interface UserService {
    UserEntity signup(SignupRequest user);
    Optional<UserEntity> getUserById(UUID id);
    Optional<UserEntity> getUserByUsername(String username);
    Optional<UserEntity> getUserByEmail(String email);
    void deleteUser(UUID id);

    void makeAdmin(String email);
}
