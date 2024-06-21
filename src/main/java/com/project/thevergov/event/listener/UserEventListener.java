package com.project.thevergov.event.listener;


import com.project.thevergov.event.UserEvent;
import com.project.thevergov.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * UserEventListener: Listens for UserEvent events and triggers corresponding actions, such as sending emails for new account verification or password reset.
 */
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final EmailService emailService;

    @EventListener // Indicates that this method is an event listener
    public void onUserEvent(UserEvent event) {
        // Use a switch statement to handle different event types
        switch (event.getType()) {
            case REGISTRATION ->
                // Send new account verification email
                    emailService.sendNewAccountEmail(
                            event.getUser().getFirstName(),
                            event.getUser().getEmail(),
                            (String) event.getData().get("key"));
            case RESETPASSWORD ->
                // Send password reset email
                    emailService.sendPasswordResetEmail(
                            event.getUser().getFirstName(),
                            event.getUser().getEmail(),
                            (String) event.getData().get("key")
                    );
            // Handle other event types or do nothing
            default -> {}
        }
    }

}
