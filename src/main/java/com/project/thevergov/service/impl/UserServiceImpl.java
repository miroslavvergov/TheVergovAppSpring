package com.project.thevergov.service.impl;


import com.project.thevergov.entity.ConfirmationEntity;
import com.project.thevergov.entity.CredentialEntity;
import com.project.thevergov.entity.RoleEntity;
import com.project.thevergov.entity.UserEntity;
import com.project.thevergov.enumeration.Authority;
import com.project.thevergov.enumeration.EventType;
import com.project.thevergov.event.UserEvent;
import com.project.thevergov.exception.ApiException;
import com.project.thevergov.repository.ConfirmationRepository;
import com.project.thevergov.repository.CredentialRepository;
import com.project.thevergov.repository.RoleRepository;
import com.project.thevergov.repository.UserRepository;
import com.project.thevergov.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.project.thevergov.utils.UserUtils.createUserEntity;


/**
 * Service implementation for managing users.
 * <p>
 * This class provides implementations for the methods defined in the UserService
 * interface. It interacts with the UserRepository to perform CRUD operations and
 * uses transactions to ensure data integrity.
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final CredentialRepository credentialRepository;

    private final ConfirmationRepository confirmationRepository;

    //private final BCryptPasswordEncoder encoder;

    private final ApplicationEventPublisher publisher;

    @Override
    public void createUser(String firstName, String lastName, String username, String email, String password) {

        UserEntity newUser = createNewUser(firstName, lastName, username, email);

        var userEntity = userRepository.save(newUser);

        var credentialEntity = new CredentialEntity(userEntity, password);
        credentialRepository.save(credentialEntity);

        var confirmationEntity = new ConfirmationEntity(userEntity);
        confirmationRepository.save(confirmationEntity);

        publisher.publishEvent(new UserEvent(userEntity, EventType.REGISTRATION, Map.of("key", confirmationEntity.getUuidKey())));
    }

    @Override
    public RoleEntity getRoleName(String name) {
        var role = roleRepository.findRoleEntityByName(name);


        return role.orElseThrow(() -> new ApiException("Role not found"));
    }

    private UserEntity createNewUser(String firstName, String lastName, String username, String email) {
        var role = getRoleName(Authority.USER.name());

        return createUserEntity(firstName, lastName, username, email, role);

    }
}


























