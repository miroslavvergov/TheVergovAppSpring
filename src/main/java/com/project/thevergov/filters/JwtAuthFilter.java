package com.project.thevergov.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.thevergov.exception.AccessDeniedException;
import com.project.thevergov.helpers.JwtHelper;
import com.project.thevergov.domain.dto.ApiErrorResponse;
import com.project.thevergov.enumeration.Role;
import com.project.thevergov.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@NoArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {

            String token = getJWTfromRequest(request);

            String username = JwtHelper.extractUsername(token);
            Role role = JwtHelper.extractRole(token);

            //If the accessToken is null. It will pass the request to next filter in the chain.
            //Any login and signup requests will not have jwt token in their header, therefore they will be passed to next filter chain.
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }
            //If any accessToken is present, then it will validate the token and then authenticate the request in security context
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                userDetailsService.setRole(role);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (JwtHelper.validateToken(token, userDetails)) {
                    GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.name());
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, List.of(authority));

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                }
            }

            filterChain.doFilter(request, response);
        } catch (AccessDeniedException e) {
            ApiErrorResponse errorResponse = new ApiErrorResponse(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(toJson(errorResponse));
        }
    }

    private String toJson(ApiErrorResponse response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            return ""; // Return an empty string if serialization fails
        }
    }

    private String getJWTfromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else {
            return null;
        }
    }
}

