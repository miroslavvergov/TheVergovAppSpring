package com.project.thevergov.service.impl;

import com.project.thevergov.exception.ApiException;
import com.project.thevergov.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.project.thevergov.utils.EmailUtils.getEmailMessage;
import static com.project.thevergov.utils.EmailUtils.getResetPasswordMessage;

/**
 * EmailServiceImpl: Implementation of the EmailService interface responsible for
 * sending various types of emails such as account verification and password reset emails.
 * <p>
 * This service utilizes JavaMailSender to send emails and handles asynchronous email
 * sending to improve application responsiveness.
 * </p>
 */
@Service // Indicates that this class is a Spring service component
@RequiredArgsConstructor // Generates a constructor with required arguments for dependency injection
@Slf4j // Provides a logger for this class
public class EmailServiceImpl implements EmailService {

    // Email subject lines used in different scenarios
    private static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    private static final String PASSWORD_RESET_REQUEST = "Reset Password Request";

    // Injected JavaMailSender instance used to send emails
    private final JavaMailSender sender;

    // Configuration properties for email sending
    @Value("${spring.mail.verify.host}") // Base URL for email verification
    private String host;
    @Value("${spring.mail.username}") // Sender's email address
    private String fromEmail;

    /**
     * Sends an email to verify a new user account.
     * <p>
     * This method constructs an email with a verification link and sends it asynchronously
     * to the specified email address.
     * </p>
     *
     * @param name  The name of the user
     * @param email The user's email address
     * @param token The verification token to include in the email
     */
    @Override
    @Async // Marks this method as asynchronous to prevent blocking the caller thread
    public void sendNewAccountEmail(String name, String email, String token) {
        try {
            // Prepare the email message
            var mailMessage = new SimpleMailMessage();
            mailMessage.setSubject(NEW_USER_ACCOUNT_VERIFICATION); // Set the subject of the email
            mailMessage.setFrom(fromEmail); // Set the sender's email address
            mailMessage.setTo(email); // Set the recipient's email address
            mailMessage.setText(getEmailMessage(name, host, token)); // Construct the email body using utility method

            // Send the email
            sender.send(mailMessage);
        } catch (Exception exception) {
            // Log the error and wrap it in a custom exception
            log.error("Error sending new account verification email: {}", exception.getMessage());
            // Consider if throwing an exception is appropriate; usually, email failures are non-critical
            throw new ApiException("Unable to send email");
        }
    }

    /**
     * Sends an email to reset a user's password.
     * <p>
     * This method constructs an email with a password reset link and sends it asynchronously
     * to the specified email address.
     * </p>
     *
     * @param name  The name of the user
     * @param email The user's email address
     * @param token The password reset token to include in the email
     */
    @Override
    @Async
    public void sendPasswordResetEmail(String name, String email, String token) {
        try {
            // Prepare the email message
            var mailMessage = new SimpleMailMessage();
            mailMessage.setSubject(PASSWORD_RESET_REQUEST); // Set the subject of the email
            mailMessage.setFrom(fromEmail); // Set the sender's email address
            mailMessage.setTo(email); // Set the recipient's email address
            mailMessage.setText(getResetPasswordMessage(name, host, token)); // Construct the email body using utility method

            // Send the email
            sender.send(mailMessage);
        } catch (Exception exception) {
            // Log the error and wrap it in a custom exception
            log.error("Error sending password reset email: {}", exception.getMessage());
            // Handling of email sending errors should consider user experience; avoid failing silently
            throw new ApiException("Unable to send email");
        }
    }
}
