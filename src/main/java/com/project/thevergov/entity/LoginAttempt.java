package com.project.thevergov.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "login_attempt", schema = "authentication")
@Getter
@Setter
@NoArgsConstructor
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "success", nullable = false)
    private boolean success;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    public LoginAttempt(String email, boolean success, LocalDateTime createdAt) {
        this.email = email;
        this.success = success;
        this.createdAt = createdAt;
    }
}
