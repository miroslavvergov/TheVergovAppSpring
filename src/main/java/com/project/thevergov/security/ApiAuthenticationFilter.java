package com.project.thevergov.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.thevergov.domain.Response;
import com.project.thevergov.dto.LoginRequest;
import com.project.thevergov.dto.User;
import com.project.thevergov.enumeration.LoginType;
import com.project.thevergov.enumeration.TokenType;
import com.project.thevergov.service.JwtService;
import com.project.thevergov.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static com.fasterxml.jackson.core.JsonParser.Feature.AUTO_CLOSE_SOURCE;
import static com.project.thevergov.constant.Constants.LOGIN_PATH;
import static com.project.thevergov.domain.VergovAuthentication.unauthenticated;
import static com.project.thevergov.utils.RequestUtils.getResponse;
import static com.project.thevergov.utils.RequestUtils.handleErrorResponse;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * ApiAuthenticationFilter: Handles authentication for user login requests.
 * <p>
 * This filter listens for POST requests to the login path, attempts to authenticate
 * the user based on credentials provided in the request, and handles successful or
 * failed authentication attempts. It also manages the creation of JWT tokens or
 * MFA setup responses.
 */
@Slf4j
public class ApiAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final UserService userService;
    private final JwtService jwtService;

    /**
     * Constructs an ApiAuthenticationFilter.
     *
     * @param authenticationManager the authentication manager to use for authenticating requests
     * @param userService           the user service for user-related operations
     * @param jwtService            the JWT service for generating and handling JWT tokens
     */
    protected ApiAuthenticationFilter(
            AuthenticationManager authenticationManager,
            UserService userService,
            JwtService jwtService) {

        super(new AntPathRequestMatcher(LOGIN_PATH, POST.name()), authenticationManager);
        this.jwtService = jwtService;
        this.userService = userService;
    }

    /**
     * Attempts to authenticate the user based on the login request.
     *
     * @param request  the HTTP request containing user credentials
     * @param response the HTTP response object
     * @return the authentication token if authentication is successful
     * @throws AuthenticationException if authentication fails
     * @throws IOException              if an I/O error occurs
     * @throws ServletException         if a servlet error occurs
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        try {
            // Parse the login request from the request body
            var user = new ObjectMapper().configure(AUTO_CLOSE_SOURCE, true)
                    .readValue(request.getInputStream(), LoginRequest.class);

            // Update login attempt in the user service
            userService.updateLoginAttempt(user.getEmail(), LoginType.LOGIN_ATTEMPT);

            // Create an authentication object and authenticate
            var authentication = unauthenticated(user.getEmail(), user.getPassword());
            return getAuthenticationManager().authenticate(authentication);
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage());
            handleErrorResponse(request, response, e);
            return null;
        }
    }

    /**
     * Handles successful authentication by providing the appropriate response.
     *
     * @param request        the HTTP request object
     * @param response       the HTTP response object
     * @param chain          the filter chain
     * @param authentication the authentication object representing the authenticated user
     * @throws IOException      if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication)
            throws IOException, ServletException {
        var user = (User) authentication.getPrincipal();
        userService.updateLoginAttempt(user.getEmail(), LoginType.LOGIN_SUCCESS);

        // Determine the appropriate response based on MFA status
        var httpResponse = user.isMfa() ? sendQrCode(request, user) : sendResponse(request, response, user);

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(OK.value());
        var out = response.getOutputStream();
        var mapper = new ObjectMapper();
        mapper.writeValue(out, httpResponse);
        out.flush();
    }

    /**
     * Sends a response with JWT tokens for a successful login.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @param user     the authenticated user
     * @return the response object containing user data and JWT tokens
     */
    private Response sendResponse(HttpServletRequest request, HttpServletResponse response, User user) {
        jwtService.addCookie(response, user, TokenType.ACCESS);
        jwtService.addCookie(response, user, TokenType.REFRESH);
        return getResponse(request, Map.of("user", user), "Login Success", OK);
    }

    /**
     * Sends a response requesting the user to enter the MFA QR code.
     *
     * @param request the HTTP request object
     * @param user    the authenticated user
     * @return the response object prompting for MFA setup
     */
    private Response sendQrCode(HttpServletRequest request, User user) {
        return getResponse(request, Map.of("user", user), "Please enter QR code", OK);
    }
}
