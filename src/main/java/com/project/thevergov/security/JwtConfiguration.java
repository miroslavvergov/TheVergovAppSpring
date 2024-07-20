package com.project.thevergov.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

/**
 * Configuration class for JWT (JSON Web Token) settings.
 * <p>
 * This class holds the configuration properties related to JWT, such as
 * the token expiration time and the secret key used for signing tokens.
 * The properties are injected from the application's external configuration
 * (e.g., application.properties or application.yml).
 * </p>
 */
@Getter
@Setter
public class JwtConfiguration {

    /**
     * The expiration time for JWT tokens, in milliseconds.
     * <p>
     * This property determines how long a JWT token remains valid before it
     * expires and needs to be refreshed. Set this value according to the
     * security requirements of your application.
     * </p>
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * The secret key used for signing JWT tokens.
     * <p>
     * This property is used to generate and verify the JWT signature. The secret
     * should be kept confidential and should be unique to your application.
     * Ensure this value is properly managed and secured, especially in production
     * environments.
     * </p>
     */
    @Value("${jwt.secret}")
    private String secret;
}
