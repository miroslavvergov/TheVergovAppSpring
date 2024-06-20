package com.project.thevergov.service.impl;

import com.project.thevergov.helpers.JwtHelper;
import com.project.thevergov.model.dto.LoginRequest;
import com.project.thevergov.model.dto.LoginResponse;
import com.project.thevergov.model.entity.LoginAttempt;
import com.project.thevergov.repository.LoginAttemptRepository;
import com.project.thevergov.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final LoginAttemptRepository repository;

    private final AuthenticationManager authenticationManager;

    private final ModelMapper modelMapper;

    @Override
    public void addLoginAttempt(String email, boolean success) {
        LoginAttempt loginAttempt = new LoginAttempt(email, success, LocalDateTime.now());
        repository.save(loginAttempt);
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (BadCredentialsException e) {
            addLoginAttempt(request.email(), false);
            throw e;
        }

        String token = JwtHelper.generateToken(request.email());
        addLoginAttempt(request.email(), true);

        LoginResponse loginResponse = modelMapper.map(request, LoginResponse.class);
        loginResponse.setToken(token);

        return loginResponse;
    }

    @Override
    public List<LoginAttempt> findRecentLoginAttempts(String email) {
        return repository.findRecent(email);
    }
}
