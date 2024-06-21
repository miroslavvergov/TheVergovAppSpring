package com.project.thevergov.constant;

/**
 * Constants: A central repository for string constants used throughout the application, particularly for managing roles and authorities.
 */
public class RoleConstants {

    // Role-related Constants

    /**
     * ROLE_PREFIX: The prefix to be added before role names.
     * This convention helps in distinguishing roles from other types of authorities.
     */
    public static final String ROLE_PREFIX = "ROLE_";

    /**
     * AUTHORITY_DELIMITER: The character used to separate multiple authorities in a comma-separated list.
     */
    public static final String AUTHORITY_DELIMITER = ",";

    // Authority Constants for Different User Roles

    /**
     * USER_AUTHORITIES: The set of permissions granted to regular users.
     * In this case, users can perform CRUD operations on articles.
     */
    public static final String USER_AUTHORITIES = "article:create,article:read,article:update,article:delete";

    /**
     * ADMIN_AUTHORITIES: The set of permissions granted to administrators.
     * Administrators have full control over articles and basic management of users (CRUD).
     */
    public static final String ADMIN_AUTHORITIES = "user:create,user:read,user:update,article:create,article:read,article:update,article:delete";

    /**
     * SUPER_ADMIN_AUTHORITIES: The set of permissions granted to super administrators.
     * Super administrators have ultimate control over both users and articles, including the ability to delete users.
     */
    public static final String SUPER_ADMIN_AUTHORITIES = "user:create,user:read,user:update,user:delete,article:create,article:read,article:update,article:delete";

    /**
     * MANAGER_AUTHORITIES: The set of permissions granted to managers.
     * In this case, managers have the same permissions as regular users, focusing on article management.
     */
    public static final String MANAGER_AUTHORITIES = "article:create,article:read,article:update,article:delete";
}
