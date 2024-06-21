package com.project.thevergov.web;


import com.project.thevergov.model.dto.ApiErrorResponse;
import com.project.thevergov.model.dto.LoginRequest;
import com.project.thevergov.model.dto.LoginResponse;
import com.project.thevergov.model.dto.SignupRequest;
import com.project.thevergov.model.entity.User;
import com.project.thevergov.service.LoginService;
import com.project.thevergov.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final LoginService loginService;


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(new ApiErrorResponse(400, result.getAllErrors().get(0).getDefaultMessage()));
        }
        try {
            User signedupUser = userService.signup(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(signedupUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse(400, e.getMessage()));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, BindingResult result) {

        //TODO implement logic to check for header of authentication - for JWT and handle it differently

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(new ApiErrorResponse(400, result.getAllErrors().get(0).getDefaultMessage()));
        }
        try {
            LoginResponse loginResponse = loginService.loginWithoutHeader(request);
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse(400, e.getMessage()));
        }
    }

}


