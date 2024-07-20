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

/**
 * Utility class for handling user-related operations, including creating user entities,
 * mapping between entities and DTOs, and generating QR codes for multi-factor authentication (MFA).
 * <p>
 * This class provides methods for:
 * - Creating a UserEntity with default values.
 * - Converting UserEntity to a User DTO.
 * - Generating QR code data and image URIs for MFA.
 * - Generating a secret for MFA.
 */
public class UserUtils {

    /**
     * Creates a new UserEntity with provided user details and default values for other fields.
     *
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param username The username of the user.
     * @param email The email of the user.
     * @param role The role assigned to the user.
     * @return A new UserEntity object with the provided details and default values.
     */
    public static UserEntity createUserEntity(String firstName, String lastName, String username, String email, RoleEntity role) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString()) // Generate a unique user ID
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .email(email)
                .lastLogin(now()) // Set current time as last login
                .accountNonExpired(true)
                .accountNonLocked(true)
                .mfa(false) // MFA is disabled by default
                .enabled(false) // Account is disabled by default
                .loginAttempts(0) // Initialize login attempts
                .qrCodeSecret(EMPTY) // Initialize QR code secret as empty
                .bio(EMPTY) // Initialize bio as empty
                .imageUrl("https://play-lh.googleusercontent.com/ki_oNQS0vtmA2eah8qbnjEhQ_ZP_f6llQ5CkNhTqvVfxRV6wtQaAxQDmq2CkjHFbeUA=w2560-h1440-rw") // Default image URL
                .role(role) // Assign role
                .build();
    }

    /**
     * Converts a UserEntity object to a User DTO, including role and credential information.
     *
     * @param userEntity The UserEntity object to convert.
     * @param role The role associated with the user.
     * @param credentialEntity The credential details of the user.
     * @return A User DTO populated with data from the UserEntity and related entities.
     */
    public static User fromUserEntity(UserEntity userEntity, RoleEntity role, CredentialEntity credentialEntity) {
        User user = new User();

        // Copy properties from UserEntity to User DTO
        BeanUtils.copyProperties(userEntity, user);

        // Set additional fields in User DTO
        user.setLastLogin(userEntity.getLastLogin().toString());
        user.setCredentialsNonExpired(isCredentialsNonExpired(credentialEntity));
        user.setCreatedAt(userEntity.getCreatedAt().toString());
        user.setUpdatedAt(userEntity.getUpdatedAt().toString());
        user.setRole(role.getName());
        user.setAuthorities(role.getAuthorities().getValue());

        return user;
    }

    /**
     * Checks if the credentials have expired based on the last update time.
     *
     * @param credentialEntity The CredentialEntity object containing credential details.
     * @return True if the credentials have not expired; otherwise, false.
     */
    private static boolean isCredentialsNonExpired(CredentialEntity credentialEntity) {
        return credentialEntity.getUpdatedAt().plusDays(NINETY_DAYS).isAfter(now());
    }

    /**
     * Creates QR code data for MFA based on email and QR code secret.
     */
    public static BiFunction<String, String, QrData> qrDataFunction =
            (email, qrCodeSecret) -> new QrData.Builder()
                    .issuer("Vergov") // Issuer name
                    .label(email) // Email as label
                    .secret(qrCodeSecret) // QR code secret
                    .algorithm(HashingAlgorithm.SHA1) // Hashing algorithm for QR code
                    .digits(6) // Number of digits in the generated code
                    .period(30) // Time period for the code validity
                    .build();

    /**
     * Generates a data URI for a QR code image based on the provided email and QR code secret.
     */
    public static BiFunction<String, String, String> qrCodeImageUri =
            (email, qrCodeSecret) -> {
                var data = qrDataFunction.apply(email, qrCodeSecret);
                var generator = new ZxingPngQrGenerator();
                byte[] imageData;
                try {
                    imageData = generator.generate(data); // Generate QR code image
                } catch (Exception exception) {
                    throw new ApiException("Unable to create QR code URI"); // Handle errors in QR code generation
                }
                return getDataUriForImage(imageData, generator.getImageMimeType()); // Convert image data to data URI
            };

    /**
     * Generates a new QR code secret for MFA.
     */
    public static Supplier<String> qrCodeSecret = () -> new DefaultSecretGenerator().generate();
}
