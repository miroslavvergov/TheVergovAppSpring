package com.project.thevergov.service.impl;

import com.project.thevergov.entity.UserEntity;
import com.project.thevergov.exception.DuplicateException;
import com.project.thevergov.domain.dto.SignupRequest;
import com.project.thevergov.enumeration.Role;
import com.project.thevergov.repository.UserRepository;
import com.project.thevergov.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for managing users.
 * <p>
 * This class provides implementations for the methods defined in the UserService
 * interface. It interacts with the UserRepository to perform CRUD operations and
 * uses transactions to ensure data integrity.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    /**
     * Saves a user to the database.
     * <p>
     * This method is transactional to ensure that the user is saved consistently.
     *
     * @param request the user to be saved
     * @return the saved user
     */
    @Override
    @Transactional
    public UserEntity signup(SignupRequest request) {
        String email = request.getEmail();
        String username = request.getUsername();

        Optional<UserEntity> existingUserForEmail = userRepository.findByEmail(email);
        Optional<UserEntity> existingUserForUsername = userRepository.findByUsername(username);


        if (existingUserForEmail.isPresent()) {
            throw new DuplicateException(String.format("User with the email address '%s' already exists.", email));
        } else if (existingUserForUsername.isPresent()) {
            throw new DuplicateException(String.format("User with the username '%s' already exists.", username));
        }

        UserEntity userEntity = modelMapper.map(request, UserEntity.class);

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        userEntity.setPassword(hashedPassword);

        userEntity.setRole(Role.USER);
        userEntity.setId(UUID.randomUUID());
        LocalDateTime creationTime = LocalDateTime.now();
        userEntity.setCreatedAt(creationTime);
        userEntity.setUpdatedAt(creationTime);

        return userRepository.save(userEntity);
    }


    /**
     * Saves a user to the database.
     * <p>
     * This method is transactional to ensure that the user is saved consistently.
     *
     * @param user the user to be saved
     * @return the saved user
     */


    /**
     * Retrieves a user by their ID.
     * <p>
     * This method is read-only transactional, optimizing for performance since it
     * does not modify data.
     *
     * @param id the ID of the user
     * @return an Optional containing the found user, or empty if no user found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntity> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    /**
     * Retrieves a user by their username.
     * <p>
     * This method is read-only transactional, optimizing for performance since it
     * does not modify data.
     *
     * @param username the username of the user
     * @return an Optional containing the found user, or empty if no user found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntity> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Retrieves a user by their email.
     * <p>
     * This method is read-only transactional, optimizing for performance since it
     * does not modify data.
     *
     * @param email the email of the user
     * @return an Optional containing the found user, or empty if no user found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Deletes a user by their ID.
     * <p>
     * This method is transactional to ensure that the user is deleted consistently.
     *
     * @param id the ID of the user to be deleted
     */
    @Override
    @Transactional
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

  //  @Override
  //  public void makeAdmin(String email) {
  //      Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
  //      UserEntity userEntity = null;
//
  //      if (optionalUser.isPresent()){
  //          userEntity = optionalUser.get();
  //          userEntity.setRole(Role.ADMIN);
  //      } else {
  //          throw new NotFoundException("User not found with email address: " + email);
  //      }
//
  //      userRepository.saveAndFlush(userEntity);
  //  }


}

