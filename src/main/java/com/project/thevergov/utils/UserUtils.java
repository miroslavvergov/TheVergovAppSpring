package com.project.thevergov.utils;

import com.project.thevergov.entity.RoleEntity;
import com.project.thevergov.entity.UserEntity;
import java.util.UUID;

import static java.time.LocalDateTime.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;


public class UserUtils {


    public static UserEntity createUserEntity(String firstName, String lastName,String username, String email, RoleEntity role) {
        UserEntity builtEntity = UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .email(email)
                .lastLogin(now())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .enabled(false)
                .loginAttempts(0)
                .qrCodeSecret(EMPTY)
                .bio(EMPTY)
                .imageUrl("https://play-lh.googleusercontent.com/ki_oNQS0vtmA2eah8qbnjEhQ_ZP_f6llQ5CkNhTqvVfxRV6wtQaAxQDmq2CkjHFbeUA=w2560-h1440-rw")
                .role(role)
                .build();
        return builtEntity;

    }
}
