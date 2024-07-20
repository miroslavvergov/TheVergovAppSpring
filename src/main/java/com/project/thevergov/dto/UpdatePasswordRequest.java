package com.project.thevergov.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * UpdatePasswordRequest: A Data Transfer Object (DTO) used for handling requests to update a user's password.
 * This class includes fields for the current password, new password, and confirmation of the new password,
 * with validation annotations to ensure the input data meets the required constraints.
 * Jackson annotations are used to manage JSON serialization and deserialization.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // Ignores any properties in the JSON input that are not defined in this class
public class UpdatePasswordRequest {

    /**
     * The current password of the user.
     * This field must not be empty or null.
     */
    @NotEmpty(message = "Password cannot be empty or null") // Ensures the password field is not left empty or null
    private String password;

    /**
     * The new password to be set for the user.
     * This field must not be empty or null.
     */
    @NotEmpty(message = "New password cannot be empty or null") // Ensures the newPassword field is not left empty or null
    private String newPassword;

    /**
     * The confirmation of the new password.
     * This field must not be empty or null and should match the new password.
     */
    @NotEmpty(message = "Confirm password cannot be empty or null") // Ensures the confirmNewPassword field is not left empty or null
    private String confirmNewPassword;

}
