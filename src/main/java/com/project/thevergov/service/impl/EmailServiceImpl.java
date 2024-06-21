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
 * EmailServiceImpl: Implements the EmailService interface, providing functionality to send emails for new account verification and password reset.
 */
@Service // Marks this class as a Spring service
@RequiredArgsConstructor // Automatically generates a constructor with required arguments (JavaMailSender in this case)
@Slf4j // Provides a logger (using SLF4J)
public class EmailServiceImpl implements EmailService {

    // Constants for email subject lines
    private static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    private static final String PASSWORD_RESET_REQUEST = "Reset Password Request";

    // Injected dependencies
    private final JavaMailSender sender; // For sending emails

    // Configuration values from application properties
    @Value("${spring.mail.verify.host}") // Host for email verification link
    private String host;
    @Value("${spring.mail.username}") // Sender's email address
    private String fromEmail;

    /**
     * sendNewAccountEmail: Sends an email to verify a new user account.
     *
     * @param name  The user's name
     * @param email The user's email address
     * @param token The verification token
     */
    @Override
    @Async // Runs this method asynchronously to improve responsiveness
    public void sendNewAccountEmail(String name, String email, String token) {
        try {
            // Construct the email message
            var mailMessage = new SimpleMailMessage();
            mailMessage.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(email);
            mailMessage.setText(getEmailMessage(name, host, token)); // Use the utility function to get the message content

            // Send the email
            sender.send(mailMessage);

        } catch (Exception exception) {
            log.error(exception.getMessage());
            //usually this is not advised in a production enviroment
            throw new ApiException("Unable to send email");
        }
    }

    /**
     * sendPasswordResetEmail: Sends an email for resetting a user's password.
     *
     * @param name  The user's name
     * @param email The user's email address
     * @param token The password reset token
     */
    @Override
    @Async
    public void sendPasswordResetEmail(String name, String email, String token) {
        try {
            var mailMessage = new SimpleMailMessage();
            mailMessage.setSubject(PASSWORD_RESET_REQUEST);
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(email);
            mailMessage.setText(getResetPasswordMessage(name, host, token));
            sender.send(mailMessage);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            //In most of the times we don't want to throw such exceptions
            //It would be better to wait for a customer  to say that he did not
            //receive the given email
            throw new ApiException("Unable to send email");
        }
    }
}
