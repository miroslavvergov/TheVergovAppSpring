package com.project.thevergov.service.impl;

import com.project.thevergov.model.entity.User;
import com.project.thevergov.repository.UserRepository;
import com.project.thevergov.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for managing users.
 *
 * This class provides implementations for the methods defined in the UserService
 * interface. It interacts with the UserRepository to perform CRUD operations and
 * uses transactions to ensure data integrity.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Saves a user to the database.
     *
     * This method is transactional to ensure that the user is saved consistently.
     *
     * @param user the user to be saved
     * @return the saved user
     */
    @Override
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Retrieves a user by their ID.
     *
     * This method is read-only transactional, optimizing for performance since it
     * does not modify data.
     *
     * @param id the ID of the user
     * @return an Optional containing the found user, or empty if no user found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    /**
     * Retrieves a user by their username.
     *
     * This method is read-only transactional, optimizing for performance since it
     * does not modify data.
     *
     * @param username the username of the user
     * @return an Optional containing the found user, or empty if no user found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Retrieves a user by their email.
     *
     * This method is read-only transactional, optimizing for performance since it
     * does not modify data.
     *
     * @param email the email of the user
     * @return an Optional containing the found user, or empty if no user found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Deletes a user by their ID.
     *
     * This method is transactional to ensure that the user is deleted consistently.
     *
     * @param id the ID of the user to be deleted
     */
    @Override
    @Transactional
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
}

