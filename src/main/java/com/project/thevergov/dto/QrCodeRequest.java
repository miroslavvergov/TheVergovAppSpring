package com.project.thevergov.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * QrCodeRequest: A Data Transfer Object (DTO) used for handling requests related to QR codes.
 * This class contains fields for user identification and QR code data, along with validation annotations
 * to ensure the input data adheres to required constraints.
 * It also uses Jackson annotations to manage JSON serialization and deserialization.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // Ignores any properties in the JSON input that are not defined in this class
public class QrCodeRequest {

    /**
     * The ID of the user associated with the QR code request.
     * This field must not be empty or null.
     */
    @NotEmpty(message = "User ID cannot be empty or null") // Validation annotation to ensure the userId field is not empty or null
    private String userId;

    /**
     * The QR code data.
     * This field must not be empty or null.
     */
    @NotEmpty(message = "QR code cannot be empty or null") // Validation annotation to ensure the qrCode field is not empty or null
    private String qrCode;

}
