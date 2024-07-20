package com.project.thevergov.security;

import com.project.thevergov.handler.ApiAccessDeniedHandler;
import com.project.thevergov.handler.ApiAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static com.google.common.net.HttpHeaders.X_REQUESTED_WITH;
import static com.project.thevergov.constant.Constants.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.http.HttpHeaders.*;

/**
 * Security configuration for the application, setting up CORS, session management,
 * exception handling, and authorization rules.
 */
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableWebSecurity
public class FilterChainConfiguration {

    private final ApiAccessDeniedHandler apiAccessDeniedHandler;
    private final ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;
    private final ApiHttpConfigurer apiHttpConfigurer;

    /**
     * Configures the security filter chain for the application.
     * <p>
     * - Disables CSRF protection as the application is stateless and relies on tokens for security.
     * - Configures CORS settings to control cross-origin requests and responses.
     * - Sets session management to stateless, ensuring no session is created or used.
     * - Customizes exception handling to provide specific responses for access denied and authentication entry point.
     * - Defines authorization rules for various endpoints, including public access and permissions based on roles.
     * </p>
     *
     * @param http HttpSecurity object for configuring security settings.
     * @return SecurityFilterChain object for security configuration.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disables CSRF for stateless APIs; tokens are used instead
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource())) // Configures CORS to handle cross-origin requests
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Ensures no HTTP sessions are created; relies on tokens for authentication
                .exceptionHandling(exception ->
                        exception.accessDeniedHandler(apiAccessDeniedHandler) // Custom handler for access denied responses
                                .authenticationEntryPoint(apiAuthenticationEntryPoint)) // Custom entry point for handling authentication errors
                .authorizeHttpRequests(request ->
                        request.requestMatchers(PUBLIC_URLS).permitAll() // Allows unrestricted access to public URLs
                                .requestMatchers(HttpMethod.OPTIONS).permitAll() // Permits all pre-flight OPTIONS requests
                                .requestMatchers(HttpMethod.DELETE, "/user/delete/**").hasAnyAuthority("user:delete") // Restricts DELETE operations to authorized users with specific authority
                                .requestMatchers(HttpMethod.DELETE, "/document/delete/**").hasAnyAuthority("document:delete") // Restricts DELETE operations to authorized users with specific authority
                                .anyRequest().authenticated()) // Requires authentication for all other requests
                .with(apiHttpConfigurer, Customizer.withDefaults()); // Integrates custom HTTP configurations

        return http.build(); // Builds and returns the configured SecurityFilterChain
    }

    /**
     * Configures CORS settings for the application.
     * <p>
     * - Allows credentials to be included in cross-origin requests.
     * - Specifies allowed origins for cross-origin requests; update for production environments.
     * - Defines allowed headers and exposed headers for cross-origin requests.
     * - Sets allowed methods for cross-origin requests.
     * - Configures max age for caching preflight responses.
     * </p>
     *
     * @return CorsConfigurationSource object with CORS settings.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true); // Allows credentials (e.g., cookies, authorization headers) in cross-origin requests
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000")); // Update to include production origins
        corsConfiguration.setAllowedHeaders(Arrays.asList(
                ORIGIN, ACCESS_CONTROL_ALLOW_ORIGIN, CONTENT_TYPE, ACCEPT, AUTHORIZATION, X_REQUESTED_WITH,
                ACCESS_CONTROL_REQUEST_HEADERS, ACCESS_CONTROL_ALLOW_CREDENTIALS, FILE_NAME)); // List of headers allowed in cross-origin requests
        corsConfiguration.setExposedHeaders(Arrays.asList(
                ORIGIN, ACCESS_CONTROL_ALLOW_ORIGIN, CONTENT_TYPE, ACCEPT, AUTHORIZATION, X_REQUESTED_WITH,
                ACCESS_CONTROL_REQUEST_HEADERS, ACCESS_CONTROL_ALLOW_CREDENTIALS, FILE_NAME)); // List of headers exposed in responses
        corsConfiguration.setAllowedMethods(Arrays.asList(
                GET.name(), POST.name(), PUT.name(), PATCH.name(), DELETE.name(), OPTIONS.name())); // List of HTTP methods allowed for cross-origin requests
        corsConfiguration.setMaxAge(3600L); // Sets the maximum age for caching preflight responses (1 hour)

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(BASE_PATH, corsConfiguration); // Registers CORS configuration for the base path
        return source; // Returns the configured CorsConfigurationSource
    }
}
