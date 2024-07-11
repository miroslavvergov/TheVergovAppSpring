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

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableWebSecurity
public class FilterChainConfiguration {

    private final ApiAccessDeniedHandler apiAccessDeniedHandler;

    private final ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;

    private final ApiHttpConfigurer apiHttpConfigurer;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception ->
                        exception.accessDeniedHandler(apiAccessDeniedHandler)
                                .authenticationEntryPoint(apiAuthenticationEntryPoint))
                .authorizeHttpRequests(request ->
                        request.requestMatchers(PUBLIC_URLS).permitAll()
                                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/user/delete/**").hasAnyAuthority("user:delete")
                                .requestMatchers(HttpMethod.DELETE, "/document/delete/**").hasAnyAuthority("document:delete")
                                .anyRequest().authenticated())
                .with(apiHttpConfigurer, Customizer.withDefaults());

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        //TODO
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
        //TODO
        corsConfiguration.setAllowedHeaders(Arrays.asList(ORIGIN, ACCESS_CONTROL_ALLOW_ORIGIN, CONTENT_TYPE, ACCEPT, AUTHORIZATION, X_REQUESTED_WITH, ACCESS_CONTROL_REQUEST_HEADERS, ACCESS_CONTROL_ALLOW_CREDENTIALS, FILE_NAME));
        corsConfiguration.setExposedHeaders(Arrays.asList(ORIGIN, ACCESS_CONTROL_ALLOW_ORIGIN, CONTENT_TYPE, ACCEPT, AUTHORIZATION, X_REQUESTED_WITH, ACCESS_CONTROL_REQUEST_HEADERS, ACCESS_CONTROL_ALLOW_CREDENTIALS, FILE_NAME));
        corsConfiguration.setAllowedMethods(Arrays.asList(GET.name(), POST.name(), PUT.name(), PATCH.name(), DELETE.name(), OPTIONS.name()));
        corsConfiguration.setMaxAge(3600L);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(BASE_PATH, corsConfiguration);
        return source;
    }
}
