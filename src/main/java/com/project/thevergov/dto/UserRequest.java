package com.project.thevergov.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * UserRequest: A Data Transfer Object (DTO) used for handling user data requests,
 * such as creating or updating user information. This class includes validation annotations
 * to ensure that the input data is properly validated and adheres to the required constraints.
 * Jackson annotations are used to manage JSON serialization and deserialization.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // Ignores any properties in the JSON input that are not defined in this class
public class UserRequest {

    /**
     * The first name of the user.
     * This field must not be empty or null.
     */
    @NotEmpty(message = "First name cannot be empty or null") // Ensures the firstName field is not left empty or null
    private String firstName;

    /**
     * The last name of the user.
     * This field must not be empty or null.
     */
    @NotEmpty(message = "Last name cannot be empty or null") // Ensures the lastName field is not left empty or null
    private String lastName;

    /**
     * The username of the user.
     * This field must not be empty or null.
     */
    @NotEmpty(message = "Username cannot be empty or null") // Ensures the username field is not left empty or null
    private String username;

    /**
     * The email address of the user.
     * This field must not be empty or null and must be a valid email address.
     */
    @NotEmpty(message = "Email cannot be empty or null") // Ensures the email field is not left empty or null
    @Email(message = "Invalid email address") // Ensures the email field contains a valid email address
    private String email;

    /**
     * The password for the user.
     * This field must not be empty or null.
     */
    @NotEmpty(message = "Password cannot be empty or null") // Ensures the password field is not left empty or null
    private String password;

    /**
     * A brief biography or description of the user.
     * This field is optional and can be left null.
     */
    private String bio;

}
