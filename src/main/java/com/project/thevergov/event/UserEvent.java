package com.project.thevergov.event;

import com.project.thevergov.entity.UserEntity;
import com.project.thevergov.enumeration.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * UserEvent: Represents an event related to a user within the application.
 * <p>
 * This class encapsulates details about the event including the user involved, the type of event, and any additional
 * data that might be associated with the event. It is used to facilitate communication between different parts of
 * the application that need to respond to user-related activities.
 */
@Getter
@Setter
@AllArgsConstructor
public class UserEvent {

    /**
     * The user associated with the event.
     * <p>
     * This is an instance of {@link UserEntity} representing the user who triggered the event. It provides
     * necessary user information needed for handling the event.
     */
    private UserEntity user;

    /**
     * The type of the event.
     * <p>
     * This is an instance of {@link EventType} that indicates the specific kind of event that has occurred, such
     * as registration or password reset. It determines the action to be taken in response to the event.
     */
    private EventType type;

    /**
     * Additional data related to the event.
     * <p>
     * This is a map that can contain any additional information relevant to the event. For example, it may include
     * tokens, keys, or other details required for processing the event.
     */
    private Map<?, ?> data;
}
