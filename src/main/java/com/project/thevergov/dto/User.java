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

@Data
public class User {

    private Long id;

    private Long createdBy;

    private Long updatedBy;

    private String userId;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private String bio;

    private String imageUrl;

    //just an idea still
    private String lastLogin;

    private String createdAt;

    private String updatedAt;

    private String role;

    private String authorities;

    //just an idea still
    private String qrCodeImageUri;

    //just an idea still
    private boolean accountNonExpired;

    //just an idea still
    private boolean accountNonLocked;

    private boolean enabled;

    //just an idea still
    private boolean mfa;

    private boolean credentialsNonExpired;
}
