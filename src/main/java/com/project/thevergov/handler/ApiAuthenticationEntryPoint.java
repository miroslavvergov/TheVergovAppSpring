package com.project.thevergov.handler;

import com.project.thevergov.utils.RequestUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.project.thevergov.utils.RequestUtils.handleErrorResponse;

/**
 * ApiAuthenticationEntryPoint: Custom implementation of {@link AuthenticationEntryPoint} to handle authentication failures.
 * <p>
 * This component is used by Spring Security to manage scenarios where an unauthenticated user tries to access a protected
 * resource. It responds to authentication errors by invoking a utility method to generate a standardized error response.
 * <p>
 * By implementing {@link AuthenticationEntryPoint}, this class integrates with Spring Security's authentication mechanism
 * to provide a consistent and user-friendly response when authentication is required but has failed.
 */
@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Commences the authentication process when an unauthenticated user attempts to access a protected resource.
     * <p>
     * This method is triggered by Spring Security when an authentication failure occurs. It uses the {@code handleErrorResponse}
     * method from {@link RequestUtils} to generate a standardized error response for authentication errors.
     *
     * @param request  the {@link HttpServletRequest} object that contains the request the client made to the server
     * @param response the {@link HttpServletResponse} object that contains the response the server sends to the client
     * @param authException the exception that was thrown during authentication failure
     * @throws IOException      if an input or output error occurs while handling the request
     * @throws ServletException if the request could not be handled
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        // Delegate error handling to the utility method for consistency
        handleErrorResponse(request, response, authException);
    }
}
