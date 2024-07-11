package com.project.thevergov.utils;

import com.project.thevergov.dto.User;
import com.project.thevergov.entity.CredentialEntity;
import com.project.thevergov.entity.RoleEntity;
import com.project.thevergov.entity.UserEntity;
import com.project.thevergov.exception.ApiException;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.project.thevergov.constant.Constants.NINETY_DAYS;
import static dev.samstevens.totp.util.Utils.getDataUriForImage;
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


    public static BiFunction<String, String, QrData> qrDataFunction =
            (email, qrCodeSecret) -> new QrData.Builder()
                    .issuer("Vergov")
                    .label(email)
                    .secret(qrCodeSecret)
                    .algorithm(HashingAlgorithm.SHA1)
                    .digits(6)
                    .period(30)
                    .build();

    public static BiFunction<String, String, String> qrCodeImageUri =
            (email, qrCodeSecret) -> {
                var data = qrDataFunction.apply(email, qrCodeSecret);
                var generator = new ZxingPngQrGenerator();
                byte[] imageData;
                try {
                    imageData = generator.generate(data);
                } catch (Exception exception) {
                    throw new ApiException("Unable to create QR code URI");
                }
                return getDataUriForImage(imageData, generator.getImageMimeType());
            };

    public static Supplier<String> qrCodeSecret = () -> new DefaultSecretGenerator().generate();
}
