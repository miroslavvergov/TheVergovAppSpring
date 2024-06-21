package com.project.thevergov.repository;

import com.project.thevergov.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


/**
 * Repository interface for {@link UserEntity} entity.
 *
 * This interface provides methods for performing CRUD operations on User entities,
 * as well as custom queries for finding users by username and email.
 */
//this annotation is not required
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmailIgnoreCase(String email);

    Optional<UserEntity> findUserByUserId(String userId);


}


