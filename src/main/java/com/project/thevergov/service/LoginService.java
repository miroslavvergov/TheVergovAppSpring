package com.project.thevergov.service;

import com.project.thevergov.model.dto.LoginRequest;
import com.project.thevergov.model.dto.LoginResponse;
import com.project.thevergov.model.entity.LoginAttempt;

import java.util.List;

public interface LoginService {

    void addLoginAttempt(String email, boolean success);

    List<LoginAttempt> findRecentLoginAttempts(String email);

    LoginResponse loginWithoutHeader(LoginRequest loginRequest);
}
