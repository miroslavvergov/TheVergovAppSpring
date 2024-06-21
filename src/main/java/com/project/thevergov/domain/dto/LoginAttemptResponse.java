package com.project.thevergov.domain.dto;

import com.project.thevergov.entity.LoginAttempt;

import java.time.LocalDateTime;

public record LoginAttemptResponse(
        LocalDateTime createdAt,
        boolean success) {

    public static LoginAttemptResponse convertToDTO(LoginAttempt loginAttempt) {
        return new LoginAttemptResponse(loginAttempt.getCreatedAt(), true);
    }
}
