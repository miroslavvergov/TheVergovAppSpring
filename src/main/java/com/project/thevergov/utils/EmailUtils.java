package com.project.thevergov.utils;

/**
 * EmailUtils: Provides utility functions for generating email messages and verification/reset URLs.
 */
public class EmailUtils {

    /**
     * getEmailMessage: Creates the message content for a new account verification email.
     *
     * @param name  The user's name
     * @param host  The host for the verification link
     * @param token The verification token
     * @return The formatted email message
     */
    public static String getEmailMessage(String name, String host, String token) {
        return "Hello "
                + name
                + ",\n\n Your new account has been created. Please click on the link below to verify your account.\n\n"
                + getVerificationUrl(host, token) + "\n\nThe Support Team";
    }

    /**
     * getVerificationUrl: Constructs the verification URL.
     *
     * @param host  The host for the verification link
     * @param token The verification token
     * @return The verification URL
     */
    public static String getVerificationUrl(String host, String token) {
        return host + "/verify/account?token=" + token;
    }

    /**
     * getResetPasswordMessage: Creates the message content for a password reset email.
     *
     * @param name  The user's name
     * @param host  The host for the password reset link
     * @param token The password reset token
     * @return The formatted email message
     */
    public static String getResetPasswordMessage(String name, String host, String token) {
        return "Hello "
                + name
                + ",\n\n You have initiated a password reset. Please click on the link below to reset your password.\n\n"
                + getResetPasswordUrl(host, token) + "\n\nThe Support Team";
    }

    /**
     * getResetPasswordUrl: Constructs the password reset URL.
     *
     * @param host  The host for the password reset link
     * @param token The password reset token
     * @return The password reset URL
     */
    public static String getResetPasswordUrl(String host, String token) {
        return host + "/verify/password?token=" + token;
    }
}
