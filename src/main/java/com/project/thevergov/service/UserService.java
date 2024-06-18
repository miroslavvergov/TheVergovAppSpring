package com.project.thevergov.service;

import com.project.thevergov.model.entity.User;

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
    User saveUser(User user);
    Optional<User> getUserById(UUID id);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
    void deleteUser(UUID id);
}
