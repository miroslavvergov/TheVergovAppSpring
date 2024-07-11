package com.project.thevergov.service;

import com.project.thevergov.dto.User;
import com.project.thevergov.entity.CredentialEntity;
import com.project.thevergov.entity.RoleEntity;
import com.project.thevergov.enumeration.LoginType;

/**
 * Service interface for managing users in the application.
 * <p>
 * This interface defines common operations for user management such as saving,
 * retrieving, and deleting users. It is implemented by a class that handles
 * the business logic for these operations.
 */
public interface UserService {

    void createUser(String firstName, String lastName, String username, String email, String password);

    RoleEntity getRoleName(String name);

    void verifyAccount(String key);

    void updateLoginAttempt(String email, LoginType loginType);

    User getUserByUserId(String userId);

    User getUserByEmail(String email);

    CredentialEntity getUserCredentialById(Long id);

    User setupMfa(Long id);

    User cancelMfa(Long id);

    User verifyQrCode(String userId, String qrCode);
}
