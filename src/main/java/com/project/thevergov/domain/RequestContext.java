package com.project.thevergov.domain;

/**
 * RequestContext: Manages user-specific context information for the duration of a request/thread.
 */
public class RequestContext {

    // ThreadLocal to store user ID for the current thread
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    // Private constructor to prevent instantiation
    private RequestContext() {
    }

    /**
     * start: Clears any existing user ID from the ThreadLocal.
     * Typically called at the beginning of a request to ensure a clean state.
     */
    public static void start() {
        USER_ID.remove();
    }

    /**
     * setUserId: Sets the user ID for the current thread.
     *
     * @param userId The user ID to associate with the current request.
     */
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    /**
     * getUserId: Retrieves the user ID associated with the current thread.
     *
     * @return The user ID, or null if not set.
     */
    public static Long getUserId() {
        return USER_ID.get();
    }
}
