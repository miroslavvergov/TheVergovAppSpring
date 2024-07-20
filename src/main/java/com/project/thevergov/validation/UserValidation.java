package com.project.thevergov.validation;

import com.project.thevergov.entity.UserEntity;
import com.project.thevergov.exception.ApiException;

/**
 * Utility class for validating user account statuses.
 * <p>
 * This class provides methods to check the current status of a user account
 * and ensure that it is in a valid state for authentication and other operations.
 */
public class UserValidation {

    /**
     * Verifies that the user account is in a valid state.
     * <p>
     * Checks if the user account is enabled, not expired, and not locked.
     * Throws an ApiException with an appropriate message if any of these conditions are not met.
     *
     * @param user The UserEntity object representing the user account to be validated.
     * @throws ApiException if the account is disabled, expired, or locked.
     */
    public static void verifyAccountStatus(UserEntity user) {
        // Check if the user account is enabled
        if (!user.isEnabled()) {
            throw new ApiException("Account is disabled");
        }
        // Check if the user account is not expired
        if (!user.isAccountNonExpired()) {
            throw new ApiException("Account has expired");
        }
        // Check if the user account is not locked
        if (!user.isAccountNonLocked()) {
            throw new ApiException("Account is locked");
        }
    }
}
