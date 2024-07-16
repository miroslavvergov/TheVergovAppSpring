/**
 * Utility class for handling HTTP requests and responses within the application.
 */
package com.project.thevergov.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.thevergov.domain.Response;
import com.project.thevergov.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import java.util.Map;
import java.util.function.*;

import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static java.time.LocalDateTime.*;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Utility methods for handling HTTP request and response operations.
 */
public class RequestUtils {

    /**
     * Writes a JSON response to the HttpServletResponse.
     */
    private static final BiConsumer<HttpServletResponse, Response> writeResponse =
            ((httpServletResponse, response) -> {
                try {
                    var outputStream = httpServletResponse.getOutputStream();
                    new ObjectMapper().writeValue(outputStream, response);
                    outputStream.flush();
                } catch (Exception e) {
                    throw new ApiException(e.getMessage());
                }
            });

    /**
     * Determines the reason for an error based on the exception and HTTP status.
     */
    private static final BiFunction<Exception, HttpStatus, String> errorReason = ((exception, httpStatus) -> {
        if (httpStatus.isSameCodeAs(FORBIDDEN)) {
            return "You do not have enough permission";
        }
        if (httpStatus.isSameCodeAs(UNAUTHORIZED)) {
            return "You are not logged in";
        }
        if (
                exception instanceof DisabledException ||
                        exception instanceof LockedException ||
                        exception instanceof BadCredentialsException ||
                        exception instanceof CredentialsExpiredException ||
                        exception instanceof ApiException
        ) {
            return exception.getMessage();
        }
        if (httpStatus.is5xxServerError()) {
            return "An internal server error occurred";
        } else {
            return "An error occurred. Please try again.";
        }
    });

    /**
     * Constructs a Response object for a given request with provided data, message, and status.
     *
     * @param request The HttpServletRequest object.
     * @param data    The data to include in the response.
     * @param message The message to include in the response.
     * @param status  The HTTP status of the response.
     * @return A Response object representing the HTTP response.
     */
    public static Response getResponse(HttpServletRequest request, Map<?, ?> data, String message, HttpStatus status) {
        return new Response(now().toString(), status.value(), request.getRequestURI(), HttpStatus.valueOf(status.value()), message, EMPTY, data);
    }

    /**
     * Handles and writes an error response to the HttpServletResponse based on the exception type.
     *
     * @param request   The HttpServletRequest object.
     * @param response  The HttpServletResponse object.
     * @param exception The exception that occurred.
     */
    public static void handleErrorResponse(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        if (exception instanceof AccessDeniedException) {
            Response apiResponse = getErrorResponse(request, response, exception, FORBIDDEN);
            writeResponse.accept(response, apiResponse);
        }
    }

    public static Response handleErrorResponse(String message, String exception, HttpServletRequest request, HttpStatusCode status) {
        return new Response(now().toString(), status.value(), request.getRequestURI(), HttpStatus.valueOf(status.value()), message, exception, emptyMap());
        // Add additional handlers for other exceptions if needed
    }

    /**
     * Constructs an error Response object for a given request and exception with the specified HTTP status.
     *
     * @param request   The HttpServletRequest object.
     * @param response  The HttpServletResponse object.
     * @param exception The exception that occurred.
     * @param status    The HTTP status of the error response.
     * @return A Response object representing the error response.
     */
    private static Response getErrorResponse(HttpServletRequest request, HttpServletResponse response, Exception exception, HttpStatus status) {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        return new Response(now().toString(),
                status.value(), request.getRequestURI(),
                HttpStatus.valueOf(status.value()),
                errorReason.apply(exception, status),
                getRootCauseMessage(exception),
                emptyMap()
        );
    }
}
