package com.project.thevergov.web;


import com.mysql.cj.log.Log;
import com.project.thevergov.helpers.JwtHelper;
import com.project.thevergov.model.dto.ApiErrorResponse;
import com.project.thevergov.model.dto.LoginRequest;
import com.project.thevergov.model.dto.LoginResponse;
import com.project.thevergov.model.dto.SignupRequest;
import com.project.thevergov.service.LoginService;
import com.project.thevergov.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final LoginService loginService;



    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        try {
            userService.signup(request);
            //201
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (Exception e) {
            //TODO
            //return ResponseEntity.badRequest().body(new ApiErrorResponse(400, e.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse(400, e.getMessage()));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = null;

        try {
            loginResponse = loginService.login(request);

        } catch (Exception e) {
            //TODO
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse(400, e.getMessage()));
        }

        return ResponseEntity.ok(loginResponse);

    }
}

