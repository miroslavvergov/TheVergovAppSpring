package com.project.thevergov.validation;

import com.project.thevergov.entity.UserEntity;
import com.project.thevergov.exception.ApiException;

public class UserValidation {

    public static void verifyAccountStatus(UserEntity user) {
        if (!user.isEnabled()) {
            throw new ApiException("Account is disabled");
        }
        if (!user.isAccountNonExpired()) {
            throw new ApiException("Account has expired");
        }
        if (!user.isAccountNonLocked()) {
            throw new ApiException("Account is locked");
        }
    }
}
