/*
 * This filter handles authorization based on JWT tokens for secure endpoints.
 * It validates and sets the authentication context based on the token provided in the request.
 */

package com.project.thevergov.security;

import com.project.thevergov.domain.RequestContext;
import com.project.thevergov.domain.Token;
import com.project.thevergov.domain.TokenData;
import com.project.thevergov.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;

import static com.project.thevergov.constant.Constants.PUBLIC_ROUTES;
import static com.project.thevergov.domain.VergovAuthentication.authenticated;
import static com.project.thevergov.enumeration.TokenType.ACCESS;
import static com.project.thevergov.enumeration.TokenType.REFRESH;
import static com.project.thevergov.utils.RequestUtils.handleErrorResponse;

/**
 * AuthorizationFilter is a Spring Web filter responsible for handling authorization
 * based on JWT tokens for secure endpoints.
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class AuthorizationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    /**
     * Intercepts incoming requests to extract and validate JWT tokens for authorization.
     * Sets the SecurityContext based on token validity and updates RequestContext with user ID.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Extract and validate Access Token
            var accessToken = jwtService.extractToken(request, ACCESS.getValue());
            if (accessToken.isPresent() && jwtService.getTokenData(accessToken.get(), TokenData::isValid)) {
                // Set authentication context if Access Token is valid
                SecurityContextHolder.getContext().setAuthentication(getAuthentication(accessToken.get(), request));
                RequestContext.setUserId(jwtService.getTokenData(accessToken.get(), TokenData::getUser).getId());
            } else {
                // If Access Token is not valid, try Refresh Token
                var refreshToken = jwtService.extractToken(request, REFRESH.getValue());
                if (refreshToken.isPresent() && jwtService.getTokenData(refreshToken.get(), TokenData::isValid)) {
                    // Generate new Access Token and set authentication context
                    var user = jwtService.getTokenData(refreshToken.get(), TokenData::getUser);
                    SecurityContextHolder.getContext().setAuthentication(getAuthentication(
                            jwtService.createToken(user, Token::getAccess), request));
                    jwtService.addCookie(response, user, ACCESS); // Add new Access Token to response
                    RequestContext.setUserId(user.getId()); // Update RequestContext with user ID
                } else {
                    // Clear security context if no valid tokens found
                    SecurityContextHolder.clearContext();
                }
            }
            filterChain.doFilter(request, response); // Continue filter chain
        } catch (Exception exception) {
            log.error(exception.getMessage());
            handleErrorResponse(request, response, exception); // Handle error response
        }
    }

    /**
     * Checks if the request method is OPTIONS or if the request URI is public.
     * If true, sets RequestContext's user ID to 0 (indicating anonymous user).
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        var shouldNotFilter = request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.name()) ||
                Arrays.asList(PUBLIC_ROUTES).contains(request.getRequestURI());

        if (shouldNotFilter) {
            RequestContext.setUserId(0L); // Set RequestContext user ID to 0 for public routes
        }
        return shouldNotFilter;
    }

    /**
     * Constructs and returns an Authentication object based on the token and request details.
     */
    private Authentication getAuthentication(String token, HttpServletRequest request) {
        var authentication = authenticated(
                jwtService.getTokenData(token, TokenData::getUser), // Get user details from token
                jwtService.getTokenData(token, TokenData::getAuthorities)); // Get authorities from token
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Set request details
        return authentication; // Return constructed Authentication object
    }
}
