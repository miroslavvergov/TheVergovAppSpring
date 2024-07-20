package com.project.thevergov.handler;

import com.project.thevergov.utils.RequestUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.project.thevergov.utils.RequestUtils.handleErrorResponse;

/**
 * ApiAccessDeniedHandler: Custom implementation of {@link AccessDeniedHandler} to handle access denied exceptions.
 * <p>
 * This component is responsible for managing scenarios where a user attempts to access a resource they do not have
 * permission to view. It handles the {@link AccessDeniedException} thrown by Spring Security when access control checks
 * fail.
 * <p>
 * In this implementation, the {@code handle} method delegates the error handling to a utility method to ensure consistent
 * error responses across the application.
 */
@Component
public class ApiAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Handles the access denied exceptions and sends an appropriate response to the client.
     * <p>
     * This method is called by Spring Security when an {@link AccessDeniedException} occurs. It uses the {@code handleErrorResponse}
     * method from {@link RequestUtils} to generate a standardized error response for access denial scenarios.
     *
     * @param request  the {@link HttpServletRequest} object that contains the request the client made to the server
     * @param response the {@link HttpServletResponse} object that contains the response the server sends to the client
     * @param accessDeniedException the exception that was thrown due to the access denial
     * @throws IOException      if an input or output error occurs while handling the request
     * @throws ServletException if the request could not be handled
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        // Delegate error handling to the utility method for consistency
        handleErrorResponse(request, response, accessDeniedException);
    }
}
