package com.project.thevergov.utils;

import com.project.thevergov.dto.User;
import com.project.thevergov.entity.CredentialEntity;
import com.project.thevergov.entity.RoleEntity;
import com.project.thevergov.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

import static com.project.thevergov.constant.Constants.NINETY_DAYS;
import static java.time.LocalDateTime.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;


public class UserUtils {


    public static UserEntity createUserEntity(String firstName, String lastName, String username, String email, RoleEntity role) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .email(email)
                .lastLogin(now())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .mfa(false)
                .enabled(false)
                .loginAttempts(0)
                .qrCodeSecret(EMPTY)
                .bio(EMPTY)
                .imageUrl("https://play-lh.googleusercontent.com/ki_oNQS0vtmA2eah8qbnjEhQ_ZP_f6llQ5CkNhTqvVfxRV6wtQaAxQDmq2CkjHFbeUA=w2560-h1440-rw")
                .role(role)
                .build();

    }

    public static User fromUserEntity(UserEntity userEntity, RoleEntity role, CredentialEntity credentialEntity) {

        User user = new User();

        BeanUtils.copyProperties(userEntity, user);

        user.setLastLogin(userEntity.getLastLogin().toString());
        user.setCredentialsNonExpired(isCredentialsNonExpired(credentialEntity));
        user.setCreatedAt(userEntity.getCreatedAt().toString());
        user.setUpdatedAt(userEntity.getUpdatedAt().toString());
        user.setRole(role.getName());
        user.setAuthorities(role.getAuthorities().getValue());
        return user;
    }

    private static boolean isCredentialsNonExpired(CredentialEntity credentialEntity) {
        return credentialEntity.getUpdatedAt().plusDays(NINETY_DAYS).isAfter(now());
    }
}
