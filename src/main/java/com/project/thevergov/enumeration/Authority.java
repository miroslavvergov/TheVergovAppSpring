package com.project.thevergov.enumeration;

import static com.project.thevergov.constant.Constants.*;

/**
 * Authority: An enumeration representing various authority levels within the application.
 * Each authority level is associated with a set of permissions defined in the constants.
 */
public enum Authority {

    /**
     * USER: Represents the standard user role with basic permissions.
     * Permissions are defined by USER_AUTHORITIES in the constants.
     */
    USER(USER_AUTHORITIES),

    /**
     * ADMIN: Represents an administrator role with extended permissions.
     * Permissions include both user management and article management, defined by ADMIN_AUTHORITIES in the constants.
     */
    ADMIN(ADMIN_AUTHORITIES),

    /**
     * SUPER_ADMIN: Represents a super administrator with the highest level of permissions.
     * Permissions include full control over users and articles, defined by SUPER_ADMIN_AUTHORITIES in the constants.
     */
    SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES),

    /**
     * MANAGER: Represents a managerial role with permissions similar to a standard user but may have additional managerial rights.
     * Permissions are defined by MANAGER_AUTHORITIES in the constants.
     */
    MANAGER(MANAGER_AUTHORITIES);

    private final String value; // The permissions associated with the authority level.

    /**
     * Constructor to set the permissions value for each authority level.
     *
     * @param value The permissions associated with this authority level.
     */
    Authority(String value) {
        this.value = value;
    }

    /**
     * Retrieves the permissions associated with this authority level.
     *
     * @return A string representing the permissions associated with this authority level.
     */
    public String getValue() {
        return this.value;
    }
}
