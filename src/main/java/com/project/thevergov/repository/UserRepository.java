package com.project.thevergov.repository;

import com.project.thevergov.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link User} entity.
 *
 * This interface provides methods for performing CRUD operations on User entities,
 * as well as custom queries for finding users by username and email.
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user
     * @return an Optional containing the found user, or empty if no user found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email.
     *
     * @param email the email of the user
     * @return an Optional containing the found user, or empty if no user found
     */
    Optional<User> findByEmail(String email);
}


