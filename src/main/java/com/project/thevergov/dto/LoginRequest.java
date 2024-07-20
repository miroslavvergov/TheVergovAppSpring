package com.project.thevergov.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * LoginRequest: A Data Transfer Object (DTO) used for handling login requests.
 * This class contains fields for user login credentials (email and password) with validation annotations
 * to ensure the input data meets the required constraints.
 * It also uses Jackson annotations to manage JSON serialization and deserialization.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // Ignores any properties in the JSON input that are not defined in this class
public class LoginRequest {

    /**
     * The email address of the user attempting to log in.
     * This field must not be empty or null and must be a valid email address.
     */
    @NotEmpty(message = "Email cannot be empty or null") // Ensures that the email field is not left empty or null
    @Email(message = "Invalid email address") // Validates that the email field contains a correctly formatted email address
    private String email;

    /**
     * The password of the user attempting to log in.
     * This field must not be empty or null.
     */
    @NotEmpty(message = "Password cannot be empty or null") // Ensures that the password field is not left empty or null
    private String password;

}
