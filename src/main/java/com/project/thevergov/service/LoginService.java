package com.project.thevergov.service;

import com.project.thevergov.domain.dto.LoginRequest;
import com.project.thevergov.domain.dto.LoginResponse;
import com.project.thevergov.entity.LoginAttempt;

import java.util.List;

public interface LoginService {

    void addLoginAttempt(String email, boolean success);

    List<LoginAttempt> findRecentLoginAttempts(String email);

    LoginResponse loginWithoutHeader(LoginRequest loginRequest);
}
