package com.project.thevergov.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.thevergov.entity.RoleEntity;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.EAGER;

/**
 * User: A Data Transfer Object (DTO) that represents a user within the application.
 * This class includes various fields representing user attributes, which are used for transferring user data
 * between different layers of the application.
 */
@Data
public class User {

    /**
     * The unique identifier for the user.
     */
    private Long id;

    /**
     * The ID of the user who created this user record.
     */
    private Long createdBy;

    /**
     * The ID of the user who last updated this user record.
     */
    private Long updatedBy;

    /**
     * A unique identifier for the user, distinct from the user ID.
     */
    private String userId;

    /**
     * The first name of the user.
     */
    private String firstName;

    /**
     * The last name of the user.
     */
    private String lastName;

    /**
     * The username of the user.
     */
    private String username;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * A brief biography or description of the user.
     */
    private String bio;

    /**
     * The URL of the user's profile image.
     */
    private String imageUrl;

    /**
     * The timestamp of the user's last login.
     */
    private String lastLogin;

    /**
     * The timestamp when the user account was created.
     */
    private String createdAt;

    /**
     * The timestamp when the user account was last updated.
     */
    private String updatedAt;

    /**
     * The role assigned to the user.
     */
    private String role;

    /**
     * The authorities or permissions assigned to the user.
     */
    private String authorities;

    /**
     * The URI for the user's QR code image, if applicable.
     */
    private String qrCodeImageUri;

    /**
     * Indicates whether the user's account has expired.
     */
    private boolean accountNonExpired;

    /**
     * Indicates whether the user's account is locked.
     */
    private boolean accountNonLocked;

    /**
     * Indicates whether the user is enabled.
     */
    private boolean enabled;

    /**
     * Indicates whether multi-factor authentication (MFA) is enabled for the user.
     */
    private boolean mfa;

    /**
     * Indicates whether the user's credentials have expired.
     */
    private boolean credentialsNonExpired;
}
