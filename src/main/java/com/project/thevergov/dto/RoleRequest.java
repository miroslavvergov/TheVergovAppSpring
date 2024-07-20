package com.project.thevergov.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * RoleRequest: A Data Transfer Object (DTO) used for handling requests related to user roles.
 * This class contains a single field for specifying a role, with validation annotations to ensure the input data
 * meets the required constraints.
 * It also uses Jackson annotations to manage JSON serialization and deserialization.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // Ignores any properties in the JSON input that are not defined in this class
public class RoleRequest {

    /**
     * The role to be assigned or requested.
     * This field must not be empty or null.
     */
    @NotEmpty(message = "Role cannot be empty or null") // Ensures the role field is not left empty or null
    private String role;

}
