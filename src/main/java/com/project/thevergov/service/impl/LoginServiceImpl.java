package com.project.thevergov.service.impl;

import com.project.thevergov.exception.NotFoundException;
import com.project.thevergov.helpers.JwtHelper;
import com.project.thevergov.domain.dto.LoginRequest;
import com.project.thevergov.domain.dto.LoginResponse;
import com.project.thevergov.entity.LoginAttempt;
import com.project.thevergov.entity.UserEntity;
import com.project.thevergov.enumeration.Role;
import com.project.thevergov.repository.LoginAttemptRepository;
import com.project.thevergov.repository.UserRepository;
import com.project.thevergov.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final LoginAttemptRepository loginAttemptRepository;

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final ModelMapper modelMapper;

    @Override
    public void addLoginAttempt(String email, boolean success) {
        LoginAttempt loginAttempt = new LoginAttempt(email, success, LocalDateTime.now());
        loginAttemptRepository.save(loginAttempt);
    }

    @Override
    @Transactional
    public LoginResponse loginWithoutHeader(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            addLoginAttempt(request.getEmail(), false);
            throw e;
        }
        String email = request.getEmail();
        Role role = null;
        String token = null;

        Optional<UserEntity> user =
                userRepository.findByEmail(email);
        if (user.isPresent()) {
            role = user.get().getRole();

        } else {
            throw new NotFoundException("User not found with email address: " + email);
        }
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            token = JwtHelper.generateToken(email, role);
        }else {
            token = SecurityContextHolder.getContext().getAuthentication().toString();
        }
        addLoginAttempt(request.getEmail(), true);

        LoginResponse loginResponse = modelMapper.map(request, LoginResponse.class);
        loginResponse.setToken(token);

        return loginResponse;
    }

    @Override
    public List<LoginAttempt> findRecentLoginAttempts(String email) {
        return loginAttemptRepository.findRecent(email);
    }
}
