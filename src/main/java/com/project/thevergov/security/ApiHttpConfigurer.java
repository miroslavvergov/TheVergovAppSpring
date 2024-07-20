package com.project.thevergov.security;

import com.project.thevergov.service.JwtService;
import com.project.thevergov.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * ApiHttpConfigurer: Custom configuration for HTTP security settings.
 * <p>
 * This class is used to configure security settings such as authentication providers,
 * and custom filters for handling authentication and authorization in the application.
 */
@Component
@RequiredArgsConstructor
public class ApiHttpConfigurer extends AbstractHttpConfigurer<ApiHttpConfigurer, HttpSecurity> {

    private final AuthorizationFilter authorizationFilter;
    private final ApiAuthenticationProvider apiAuthenticationProvider;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationConfiguration authenticationConfiguration;

    /**
     * Initializes security settings with the custom authentication provider.
     *
     * @param http the HttpSecurity instance to configure
     * @throws Exception if an error occurs during configuration
     */
    @Override
    public void init(HttpSecurity http) throws Exception {
        http.authenticationProvider(apiAuthenticationProvider);
    }

    /**
     * Configures the HTTP security settings.
     * <p>
     * Adds custom filters for authorization and authentication handling.
     *
     * @param http the HttpSecurity instance to configure
     * @throws Exception if an error occurs during configuration
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        // Add custom authorization filter before the default UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);

        // Add custom authentication filter after the default UsernamePasswordAuthenticationFilter
        http.addFilterAfter(
                new ApiAuthenticationFilter(
                        authenticationConfiguration.getAuthenticationManager(),
                        userService,
                        jwtService
                ),
                UsernamePasswordAuthenticationFilter.class
        );
    }
}
