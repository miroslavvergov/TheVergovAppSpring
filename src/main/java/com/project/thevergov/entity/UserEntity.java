package com.project.thevergov.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;
import static jakarta.persistence.FetchType.*;

/**
 * Represents a user in the application.
 * <p>
 * Each user has a unique username and email, and a password for authentication. Users are assigned roles
 * which dictate their permissions within the application.
 */

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@JsonInclude(NON_DEFAULT)
public class UserEntity extends Auditable {

    @Column(updatable = false, unique = true, nullable = false)
    private String userId;


    private String firstName;

    private String lastName;

    private String username;

    @Column(updatable = false, unique = true, nullable = false)
    private String email;

    private String password;

    //reimplement login attempt in here
    private Integer loginAttempts;

    //reimplement login attempt in here
    private LocalDateTime lastLogin;

    private String phone;

    private String bio;

    private String imageUrl;

    //just an idea still
    private boolean accountNonExpired;

    //just an idea still
    private boolean accountNonLocked;

    private boolean enabled;

    //just an idea still
    private boolean mfa;

    //just an idea still
    @JsonIgnore
    private String qrCodeSecret;

    //just an idea still
    @Column(columnDefinition = "TEXT")
    private String qrCodeImageUri;

    @ManyToOne(fetch = EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"
            )

    )
    private RoleEntity role;
}

