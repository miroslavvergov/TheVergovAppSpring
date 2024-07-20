package com.project.thevergov.domain;

import com.project.thevergov.dto.User;
import io.jsonwebtoken.Claims;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * TokenData: A class representing the data associated with a token.
 * This class includes user information, JWT claims, token validity status, and user authorities.
 * The class uses Lombok annotations to generate boilerplate code such as getters, setters, and a builder.
 */
@Builder
@Getter
@Setter
public class TokenData {

    // User information associated with the token
    private User user;

    // JWT claims extracted from the token
    private Claims claims;

    // Flag indicating whether the token is valid
    private boolean valid;

    // List of authorities granted to the user
    private List<GrantedAuthority> authorities;
}
