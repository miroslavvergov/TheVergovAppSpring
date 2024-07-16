package com.project.thevergov.constant;

/**
 * Constants: A central repository for string constants used throughout the application, particularly for managing roles and authorities.
 */
public class Constants {

    public static final int NINETY_DAYS = 90;

    public static final String PHOTO_STORAGE_PATH = System.getProperty("user.home") + "/Downloads/uploads/";

    public static final int STRENGTH = 12;

    //TODO
    public static final String[] PUBLIC_URLS = {};

    public static final String BASE_PATH = "/**";

    public static final String FILE_NAME = "File-Name";

    //TODO
    public static final String[] PUBLIC_ROUTES = {};

    public static final String LOGIN_PATH = "/user/login";
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


    // QUERIES

    public static final String SELECT_PAPERS_QUERY =
            "SELECT paper.id, " +
                    "    paper.paper_id, " +
                    "    paper.name, " +
                    "    paper.description, " +
                    "    paper.uri, " +
                    "    paper.icon, " +
                    "    paper.size, " +
                    "    paper.formatted_size, " +
                    "    paper.extension, " +
                    "    paper.reference_id, " +
                    "    paper.created_at, " +
                    "    paper.updated_at, " +
                    "    CONCAT(owner.first_name, ' ', owner.last_name) AS owner_name, " +
                    "    owner.email AS owner_email, " +  // Modified line
                    "    owner.phone AS owner_phone, " +  // Modified line
                    "    owner.last_login AS owner_last_login, " +
                    "    CONCAT(updater.first_name, ' ', updater.last_name) AS updater_name " +
                    "FROM papers paper " +
                    "JOIN users owner ON owner.id = paper.created_by " +
                    "JOIN users updater ON updater.id = paper.updated_by;"; // Added semicolon


    public static final String SELECT_COUNT_PAPERS_QUERY = "SELECT COUNT(*) FROM papers";


    public static final String SELECT_PAPERS_BY_NAME_QUERY =
            "SELECT paper.id, " +
                    "    paper.paper_id, " +
                    "    paper.name, " +
                    "    paper.description, " +
                    "    paper.uri, " +
                    "    paper.icon, " +
                    "    paper.size, " +
                    "    paper.formatted_size, " +
                    "    paper.extension, " +
                    "    paper.reference_id, " +
                    "    paper.created_at, " +
                    "    paper.updated_at, " +
                    "    CONCAT(owner.first_name, ' ', owner.last_name) AS owner_name, " +
                    "    owner.email AS owner_email, " +
                    "    owner.phone AS owner_phone, " +
                    "    owner.last_login AS owner_last_login, " +
                    "    CONCAT(updater.first_name, ' ', updater.last_name) AS updater_name " +
                    "FROM papers paper " +
                    "JOIN users owner ON owner.id = paper.created_by " +
                    "JOIN users updater ON updater.id = paper.updated_by " +
                    "WHERE name ~* :paperName ";


    public static final String SELECT_COUNT_PAPERS_BY_NAME_QUERY = "SELECT COUNT(*) FROM papers WHERE name ~* :paperName";


    public static final String SELECT_PAPER_QUERY =
            "SELECT paper.id, " +
                    "    paper.paper_id, " +
                    "    paper.name, " +
                    "    paper.description, " +
                    "    paper.uri, " +
                    "    paper.icon, " +
                    "    paper.size, " +
                    "    paper.formatted_size, " +
                    "    paper.extension, " +
                    "    paper.reference_id, " +
                    "    paper.created_at, " +
                    "    paper.updated_at, " +
                    "    CONCAT(owner.first_name, ' ', owner.last_name) AS owner_name, " +
                    "    owner.email AS owner_email, " +
                    "    owner.phone AS owner_phone, " +
                    "    owner.last_login AS owner_last_login, " +
                    "    CONCAT(updater.first_name, ' ', updater.last_name) AS updater_name " +
                    "FROM papers paper " +
                    "JOIN users owner ON owner.id = paper.created_by " +
                    "JOIN users updater ON updater.id = paper.updated_by " +
                    "WHERE paper.paper_id = ?1";
}
