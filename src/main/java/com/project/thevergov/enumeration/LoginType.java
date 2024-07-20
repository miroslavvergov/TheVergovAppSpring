package com.project.thevergov.enumeration;

/**
 * LoginType: An enumeration representing different types of login-related events.
 * This enum is used to categorize the various stages or outcomes of a login process.
 */
public enum LoginType {

    /**
     * LOGIN_ATTEMPT: Represents an attempt made by a user to log in to the application.
     * This event occurs when the user provides credentials to access their account.
     */
    LOGIN_ATTEMPT,

    /**
     * LOGIN_SUCCESS: Represents a successful login event where the user's credentials are validated,
     * and access to the application is granted.
     */
    LOGIN_SUCCESS;
}
