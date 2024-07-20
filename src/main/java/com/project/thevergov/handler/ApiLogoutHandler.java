package com.project.thevergov.handler;

import com.project.thevergov.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import static com.project.thevergov.enumeration.TokenType.ACCESS;
import static com.project.thevergov.enumeration.TokenType.REFRESH;

/**
 * ApiLogoutHandler: Custom implementation of {@link LogoutHandler} to manage user logout functionality.
 * <p>
 * This class handles the logout process by clearing the security context and removing JWT tokens from cookies.
 * It integrates with {@link JwtService} to ensure that authentication tokens are properly invalidated and removed
 * from the user's cookies upon logout.
 */
@RequiredArgsConstructor
@Service
public class ApiLogoutHandler implements LogoutHandler {

    private final JwtService jwtService;

    /**
     * Handles the logout process by clearing the security context and removing authentication tokens.
     * <p>
     * This method is called during the logout process. It uses {@link SecurityContextLogoutHandler} to clear
     * the security context and then invokes {@link JwtService#removeCookie(HttpServletRequest, HttpServletResponse, String)}
     * to remove both the access and refresh tokens from the user's cookies.
     *
     * @param request        the {@link HttpServletRequest} object that contains the request the client made to the server
     * @param response       the {@link HttpServletResponse} object that contains the response the server sends to the client
     * @param authentication the {@link Authentication} object representing the current user's authentication
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Clear the security context to remove authentication information
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, authentication);

        // Remove access and refresh tokens from cookies
        jwtService.removeCookie(request, response, ACCESS.getValue());
        jwtService.removeCookie(request, response, REFRESH.getValue());
    }
}
