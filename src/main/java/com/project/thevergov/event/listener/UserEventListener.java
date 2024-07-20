package com.project.thevergov.event.listener;

import com.project.thevergov.event.UserEvent;
import com.project.thevergov.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * UserEventListener: A component that listens for UserEvent events and performs actions based on the event type.
 * <p>
 * This listener is responsible for handling user-related events such as registration and password reset
 * by triggering corresponding email notifications through the EmailService.
 */
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final EmailService emailService;

    /**
     * Handles UserEvent events by checking the type of the event and performing the corresponding action.
     * <p>
     * Depending on the type of event (e.g., REGISTRATION or RESETPASSWORD), this method sends appropriate emails
     * using the EmailService. It retrieves necessary details from the event object and passes them to the service
     * for email sending.
     *
     * @param event The UserEvent object containing details about the event.
     */
    @EventListener
    public void onUserEvent(UserEvent event) {
        // Determine action based on the event type
        switch (event.getType()) {
            case REGISTRATION ->
                // Handle new account registration by sending a verification email
                    emailService.sendNewAccountEmail(
                            event.getUser().getFirstName(),
                            event.getUser().getEmail(),
                            (String) event.getData().get("key")
                    );

            case RESETPASSWORD ->
                // Handle password reset request by sending a reset email
                    emailService.sendPasswordResetEmail(
                            event.getUser().getFirstName(),
                            event.getUser().getEmail(),
                            (String) event.getData().get("key")
                    );

            // Default case to handle any unexpected event types
            default -> {
                // Optionally log or handle unknown event types if necessary
            }
        }
    }
}
