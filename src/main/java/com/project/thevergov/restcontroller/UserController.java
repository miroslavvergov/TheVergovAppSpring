package com.project.thevergov.restcontroller;

import com.project.thevergov.domain.Response;
import com.project.thevergov.dto.*;
import com.project.thevergov.enumeration.TokenType;
import com.project.thevergov.service.JwtService;
import com.project.thevergov.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static com.project.thevergov.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = {"/user"})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<Response> saveUser(
            @RequestBody @Valid UserRequest user,
            HttpServletRequest request) {

        userService.createUser(
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword());

        return ResponseEntity
                .created(getUri())
                .body(getResponse(request, emptyMap(), "Account created. Check your email to enable your account", CREATED));
    }

    @PatchMapping("/mfa/setup")
    public ResponseEntity<Response> setupMfa(
            @AuthenticationPrincipal User userPrincipal,
            HttpServletRequest httpServletRequest) {

        var user = userService.setupMfa(userPrincipal.getId());

        return ResponseEntity
                .ok()
                .body(getResponse(httpServletRequest, Map.of("user", user), "MFA set up successfully", OK));
    }

    @PatchMapping("/mfa/cancel")
    public ResponseEntity<Response> cancelMfa(
            @AuthenticationPrincipal User userPrincipal,
            HttpServletRequest httpServletRequest) {

        var user = userService.cancelMfa(userPrincipal.getId());

        return ResponseEntity
                .ok()
                .body(getResponse(httpServletRequest, Map.of("user", user), "MFA canceled successfully", OK));

    }

    @PatchMapping("/verify/qrcode")
    public ResponseEntity<Response> verifyQrcode(
            @RequestBody QrCodeRequest qrCodeRequest,
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        var user = userService.verifyQrCode(qrCodeRequest.getUserId(), qrCodeRequest.getQrCode());
        jwtService.addCookie(response, user, TokenType.ACCESS);
        jwtService.addCookie(response, user, TokenType.REFRESH);
        return ResponseEntity
                .ok()
                .body(
                        getResponse(
                                request,
                                Map.of("user", user),
                                "QR code verified", OK));

    }

    @GetMapping("/verify/account")
    public ResponseEntity<Response> verifyAccount(@RequestParam("key") String key, HttpServletRequest request) {
        userService.verifyAccount(key);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account verified.", OK));
    }

    @GetMapping("/profile")
    public ResponseEntity<Response> profile(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        User user = userService.getUserByUserId(userPrincipal.getUserId());
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "Profile retrieved", OK));
    }

    @PostMapping("/resetpassword")
    public ResponseEntity<Response> resetPassword(
            @RequestBody @Valid EmailRequest emailRequest,
            HttpServletRequest request) {

        userService.resetPassword(
                emailRequest.getEmail());

        return ResponseEntity
                .created(getUri())
                .body(getResponse(request, emptyMap(),
                        "An email has been sent to reset your password.",
                        OK));
    }

    @GetMapping("/verify/password")
    public ResponseEntity<Response> verifyPasswordKey(
            //TODO rename to token
            @RequestParam("key") String key,
            HttpServletRequest request) {

        var user = userService.verifyPasswordKey(
                key);

        return ResponseEntity
                .created(getUri())
                .body(getResponse(request,
                        Map.of("user", user),
                        "Enter new password.",
                        OK));
    }

    @PostMapping("/resetpassword")
    public ResponseEntity<Response> doResetPassword(
            @RequestBody @Valid ResetPasswordRequest resetPasswordRequest,
            HttpServletRequest request) {

        userService.updatePassword(
                resetPasswordRequest.getUserId(),
                resetPasswordRequest.getNewPassword(),
                resetPasswordRequest.getConfirmNewPassword()
        );

        return ResponseEntity
                .created(getUri())
                .body(getResponse(
                        request,
                        emptyMap(),
                        "Password reset successfully",
                        OK));
    }

    private URI getUri() {
        return URI.create("");
    }


}






























