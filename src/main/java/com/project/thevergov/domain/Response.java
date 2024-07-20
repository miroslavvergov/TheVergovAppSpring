package com.project.thevergov.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

/**
 * Response: A record class representing a standard HTTP response.
 * This class is used to encapsulate the details of the response sent back to the client.
 */
@JsonInclude(NON_DEFAULT) // Only include non-default values in the JSON representation
public record Response(
        String time,         // The timestamp of when the response was created
        int code,            // The HTTP status code
        String path,         // The request path that generated the response
        HttpStatus status,   // The HTTP status (e.g., OK, BAD_REQUEST)
        String message,      // A descriptive message about the response
        String exception,    // Details about any exception that occurred (if applicable)
        Map<?, ?> data) {    // Additional data to include in the response
}
