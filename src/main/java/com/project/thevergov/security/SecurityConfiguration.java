package com.project.thevergov.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static com.project.thevergov.constant.Constants.STRENGTH;

/**
 * Configuration class for Spring Security components.
 * <p>
 * This class provides configuration for security-related beans used in the
 * application, including the password encoder. The password encoder is used
 * for hashing and verifying passwords in a secure manner.
 * </p>
 */
@Configuration
public class SecurityConfiguration {

    /**
     * Provides a BCryptPasswordEncoder bean for encoding and validating passwords.
     * <p>
     * BCryptPasswordEncoder is a password encoder that uses the BCrypt hashing
     * algorithm. The strength parameter specifies the log rounds used to create
     * the BCrypt hash. The higher the number, the more computationally expensive
     * the hash computation is, which increases security but also requires more
     * processing power.
     * </p>
     *
     * @return A BCryptPasswordEncoder instance configured with the specified strength.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        // Strength is set to a value defined in Constants.STRENGTH.
        // A value of 12 is commonly used, providing a good balance between security and performance.
        return new BCryptPasswordEncoder(STRENGTH);
    }
}
