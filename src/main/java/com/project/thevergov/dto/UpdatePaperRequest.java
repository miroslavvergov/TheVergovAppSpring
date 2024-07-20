package com.project.thevergov.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * UpdatePaperRequest: A Data Transfer Object (DTO) used for handling requests to update a paper's details.
 * This class contains fields for specifying the paper's ID, name, and description, with validation annotations
 * to ensure that the input data meets the required constraints.
 * It also uses Jackson annotations to manage JSON serialization and deserialization.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // Ignores any properties in the JSON input that are not defined in this class
public class UpdatePaperRequest {

    /**
     * The unique identifier of the paper to be updated.
     * This field must not be empty or null.
     */
    @NotEmpty(message = "Paper Id cannot be empty or null") // Ensures the paperId field is not left empty or null
    private String paperId;

    /**
     * The name of the paper to be updated.
     * This field must not be empty or null.
     */
    @NotEmpty(message = "Name cannot be empty or null") // Ensures the name field is not left empty or null
    private String name;

    /**
     * The description of the paper to be updated.
     * This field must not be empty or null.
     */
    @NotEmpty(message = "Description cannot be empty or null") // Ensures the description field is not left empty or null
    private String description;

}
