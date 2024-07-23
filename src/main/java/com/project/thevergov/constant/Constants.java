package com.project.thevergov.constant;

/**
 * Constants: A central repository for string constants used throughout the application, particularly for managing roles and authorities.
 */
public class Constants {

    // Time Constants
    public static final int NINETY_DAYS = 90;

    // File Storage Constants
    public static final String FILE_STORAGE = System.getProperty("user.home") + "/Downloads/uploads/";

    // Security and Strength Constants
    public static final int STRENGTH = 12;

    // URL Constants
    public static final String BASE_PATH = "/**";
    public static final String LOGIN_PATH = "/user/login";

    // Public URL Endpoints
    public static final String[] PUBLIC_URLS = {
            "/user/reset-password/reset/**",
            "/user/verify/reset-password/**",
            "/user/reset-password/**",
            "/user/verify/qrcode/**",
            "/user/login/**",
            "/user/verify/account/**",
            "/user/register/**",
            "/user/new/password/**",
            "/user/verify/**",
            "/user/reset-password/**",
            "/user/image/**",
            "/user/verify/password/**"
    };

    // Public Routes
    public static final String[] PUBLIC_ROUTES = {
            "/user/reset-password/reset",
            "/user/verify/reset-password",
            "/user/reset-password",
            "/user/verify/qrcode",
            "/user/stream",
            "/user/id",
            "/user/login",
            "/user/register",
            "/user/new/password",
            "/user/verify",
            "/user/refresh/token",
            "/user/reset-password",
            "/user/image",
            "/user/verify/account",
            "/user/verify/password",
            "/user/verify/code"
    };

    // Header Constants
    public static final String FILE_NAME = "File-Name";

    // Miscellaneous Constants
    public static final String AUTHORITIES = "authorities";
    public static final String EMPTY_VALUE = "empty";
    public static final String ROLE = "role";
    public static final String THE_VERGOV_APP = "THE_VERGOV_APP";

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
    public static final String USER_AUTHORITIES = "paper:create,paper:read,paper:update,paper:delete";

    /**
     * ADMIN_AUTHORITIES: The set of permissions granted to administrators.
     * Administrators have full control over articles and basic management of users (CRUD).
     */
    public static final String ADMIN_AUTHORITIES = "user:create,user:read,user:update,paper:create,paper:read,paper:update,paper:delete";

    /**
     * SUPER_ADMIN_AUTHORITIES: The set of permissions granted to super administrators.
     * Super administrators have ultimate control over both users and articles, including the ability to delete users.
     */
    public static final String SUPER_ADMIN_AUTHORITIES = "user:create,user:read,user:update,user:delete,paper:create,paper:read,paper:update,paper:delete";

    /**
     * MANAGER_AUTHORITIES: The set of permissions granted to managers.
     * In this case, managers have the same permissions as regular users, focusing on article management.
     */
    public static final String MANAGER_AUTHORITIES = "paper:create,paper:read,paper:update,paper:delete";

    // SQL Query Constants
    public static final String SELECT_PAPERS_QUERY =
            "SELECT paper.id, " +
                    "       paper.paper_id, " +
                    "       paper.name, " +
                    "       paper.description, " +
                    "       paper.uri, " +
                    "       paper.icon, " +
                    "       paper.size, " +
                    "       paper.formatted_size, " +
                    "       paper.extension, " +
                    "       paper.reference_id, " +
                    "       paper.created_at, " +
                    "       paper.updated_at, " +
                    "       CONCAT(owner.first_name, ' ', owner.last_name) AS owner_name, " +
                    "       owner.email AS owner_email, " +
                    "       owner.last_login AS owner_last_login, " +
                    "       CONCAT(updater.first_name, ' ', updater.last_name) AS updater_name " +
                    "FROM papers paper " +
                    "JOIN users owner ON owner.id = paper.created_by " +
                    "JOIN users updater ON updater.id = paper.updated_by;";

    public static final String SELECT_COUNT_PAPERS_QUERY =
            "SELECT COUNT(*) FROM papers";

    public static final String SELECT_PAPERS_BY_NAME_QUERY =
            "SELECT paper.id, " +
                    "       paper.paper_id, " +
                    "       paper.name, " +
                    "       paper.description, " +
                    "       paper.uri, " +
                    "       paper.icon, " +
                    "       paper.size, " +
                    "       paper.formatted_size, " +
                    "       paper.extension, " +
                    "       paper.reference_id, " +
                    "       paper.created_at, " +
                    "       paper.updated_at, " +
                    "       CONCAT(owner.first_name, ' ', owner.last_name) AS owner_name, " +
                    "       owner.email AS owner_email, " +
                    "       owner.last_login AS owner_last_login, " +
                    "       CONCAT(updater.first_name, ' ', updater.last_name) AS updater_name " +
                    "FROM papers paper " +
                    "JOIN users owner ON owner.id = paper.created_by " +
                    "JOIN users updater ON updater.id = paper.updated_by " +
                    "WHERE name ~* :paperName";

    public static final String SELECT_COUNT_PAPERS_BY_NAME_QUERY =
            "SELECT COUNT(*) FROM papers WHERE name ~* :paperName";

    public static final String SELECT_PAPER_QUERY =
            "SELECT paper.id, " +
                    "       paper.paper_id, " +
                    "       paper.name, " +
                    "       paper.description, " +
                    "       paper.uri, " +
                    "       paper.icon, " +
                    "       paper.size, " +
                    "       paper.formatted_size, " +
                    "       paper.extension, " +
                    "       paper.reference_id, " +
                    "       paper.created_at, " +
                    "       paper.updated_at, " +
                    "       CONCAT(owner.first_name, ' ', owner.last_name) AS owner_name, " +
                    "       owner.email AS owner_email, " +
                    "       owner.last_login AS owner_last_login, " +
                    "       CONCAT(updater.first_name, ' ', updater.last_name) AS updater_name " +
                    "FROM papers paper " +
                    "JOIN users owner ON owner.id = paper.created_by " +
                    "JOIN users updater ON updater.id = paper.updated_by " +
                    "WHERE paper.paper_id = ?1";
}
