package com.project.thevergov.domain;

import com.project.thevergov.dto.User;
import com.project.thevergov.exception.ApiException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

/**
 * VergovAuthentication: A custom authentication token used for managing user authentication.
 * This class extends AbstractAuthenticationToken and provides custom implementations for authentication states.
 */
public class VergovAuthentication extends AbstractAuthenticationToken {

    // Constants for protecting sensitive information
    private static final String PASSWORD_PROTECTED = "{PASSWORD PROTECTED}";
    private static final String EMAIL_PROTECTED = "{EMAIL PROTECTED}";

    // User object containing user details
    private User user;

    // User's email address
    private String email;

    // User's password
    private String password;

    // Flag indicating whether the user is authenticated
    private Boolean authenticated;

    /**
     * Private constructor for creating an unauthenticated token with email and password.
     *
     * @param email the user's email
     * @param password the user's password
     */
    private VergovAuthentication(String email, String password) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.password = password;
        this.email = email;
        this.authenticated = false;
    }

    /**
     * Private constructor for creating an authenticated token with user details and authorities.
     *
     * @param user the authenticated user
     * @param authorities the authorities granted to the user
     */
    private VergovAuthentication(User user, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.user = user;
        this.password = PASSWORD_PROTECTED;
        this.email = EMAIL_PROTECTED;
        this.authenticated = true;
    }

    /**
     * Factory method to create an unauthenticated VergovAuthentication token.
     *
     * @param email the user's email
     * @param password the user's password
     * @return a new instance of VergovAuthentication representing an unauthenticated state
     */
    public static VergovAuthentication unauthenticated(String email, String password) {
        return new VergovAuthentication(email, password);
    }

    /**
     * Factory method to create an authenticated VergovAuthentication token.
     *
     * @param user the authenticated user
     * @param authorities the authorities granted to the user
     * @return a new instance of VergovAuthentication representing an authenticated state
     */
    public static VergovAuthentication authenticated(User user, Collection<? extends GrantedAuthority> authorities) {
        return new VergovAuthentication(user, authorities);
    }

    /**
     * Returns a protected string instead of the actual credentials.
     *
     * @return a protected string indicating credentials are not exposed
     */
    @Override
    public Object getCredentials() {
        return PASSWORD_PROTECTED;
    }

    /**
     * Returns the principal (the authenticated user).
     *
     * @return the authenticated user
     */
    @Override
    public Object getPrincipal() {
        return this.user;
    }

    /**
     * Retrieves the user's password.
     *
     * @return the user's password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Retrieves the user's email address.
     *
     * @return the user's email address
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Prevents setting the authenticated state from outside the class.
     *
     * @param authenticated the new authentication state
     */
    @Override
    public void setAuthenticated(boolean authenticated) {
        throw new ApiException("You cannot set authentication");
    }

    /**
     * Checks if the user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise
     */
    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }
}
