package com.project.thevergov.enumeration;

/**
 * TokenType: An enumeration representing the different types of tokens used in the application.
 * <p>
 * Tokens are used for managing user sessions and authentication. Each type of token serves a specific
 * purpose within the authentication and authorization processes.
 */
public enum TokenType {

    /**
     * ACCESS: Represents an access token, typically used for granting access to protected resources or
     * APIs. This token is often short-lived and used to authenticate the user's session.
     */
    ACCESS("access-token"),

    /**
     * REFRESH: Represents a refresh token, used to obtain a new access token once the original access
     * token expires. Refresh tokens are generally long-lived and are used to maintain a user's session
     * without requiring them to log in again.
     */
    REFRESH("refresh-token");

    private final String value;

    /**
     * Constructor for TokenType.
     *
     * @param value The string representation of the token type.
     */
    TokenType(String value) {
        this.value = value;
    }

    /**
     * Gets the string value associated with the token type.
     *
     * @return The string representation of the token type.
     */
    public String getValue() {
        return this.value;
    }
}
