package com.project.thevergov.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * ResetPasswordRequest: A Data Transfer Object (DTO) used for handling password reset requests.
 * This class encapsulates the necessary fields for resetting a user's password, including validation annotations
 * to ensure the input data meets the required constraints.
 * It also uses Jackson annotations to manage JSON serialization and deserialization.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // Ignores any properties in the JSON input that are not defined in this class
public class ResetPasswordRequest {

    /**
     * The unique identifier of the user requesting the password reset.
     * This field must not be empty or null.
     */
    @NotEmpty(message = "User ID cannot be empty or null") // Ensures the userId field is not left empty or null
    private String userId;

    /**
     * The new password to be set for the user.
     * This field must not be empty or null.
     */
    @NotEmpty(message = "Password cannot be empty or null") // Ensures the newPassword field is not left empty or null
    private String newPassword;

    /**
     * The confirmation of the new password.
     * This field must not be empty or null and should match the new password.
     */
    @NotEmpty(message = "Confirm password cannot be empty or null") // Ensures the confirmNewPassword field is not left empty or null
    private String confirmNewPassword;

}
