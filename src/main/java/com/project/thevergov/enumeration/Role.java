package com.project.thevergov.enumeration;

/**
 * Role: An enumeration representing the different roles a user can have within the application.
 * <p>
 * Roles are used to manage user permissions and control access to various features and functionalities
 * within the application. Each role has specific permissions associated with it.
 * <p>
 * Note: The application can have only one OWNER role. This role is typically assigned to the primary
 * individual or entity responsible for managing the entire system.
 */
public enum Role {
    /**
     * USER: Represents a standard user role with basic permissions to interact with the application's
     * core features. Users can perform standard operations but may have restricted access compared to
     * higher-level roles.
     */
    USER,

    /**
     * ADMIN: Represents an administrative role with elevated permissions compared to the standard user.
     * Administrators have access to advanced features and management functions, allowing them to
     * perform tasks such as user management and configuration changes.
     */
    ADMIN,

    /**
     * OWNER: Represents a unique role assigned to a single individual or entity who has the highest level
     * of control over the application. The OWNER role is typically responsible for overall system
     * management and decision-making.
     * <p>
     * Note: The application can only have one OWNER role, as specified by the system requirements.
     * </p>
     */
    OWNER
}
