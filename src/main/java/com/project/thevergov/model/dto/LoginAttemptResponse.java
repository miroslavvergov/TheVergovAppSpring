package com.project.thevergov.model.dto;

import com.project.thevergov.model.entity.LoginAttempt;

import java.time.LocalDateTime;

public record LoginAttemptResponse(
        LocalDateTime createdAt,
        boolean success) {

    public static LoginAttemptResponse convertToDTO(LoginAttempt loginAttempt) {
        return new LoginAttemptResponse(loginAttempt.getCreatedAt(), true);
    }
}
