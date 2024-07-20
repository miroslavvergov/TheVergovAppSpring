package com.project.thevergov.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * EmailRequest: A Data Transfer Object (DTO) used for handling email-related requests.
 * This class contains validation annotations to ensure that the provided email is not empty and is in a valid format.
 * It also uses Jackson annotations to manage JSON serialization and deserialization.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // Ignores unknown properties during JSON deserialization
public class EmailRequest {

    /**
     * The email address associated with the request.
     * This field must not be empty or null, and must be a valid email address.
     */
    @NotEmpty(message = "Email cannot be empty or null") // Validation annotation to ensure the email field is not empty
    @Email(message = "Invalid email address") // Validation annotation to ensure the email field contains a valid email address
    private String email;

}
