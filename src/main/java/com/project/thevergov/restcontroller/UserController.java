package com.project.thevergov.restcontroller;

import com.project.thevergov.domain.Response;
import com.project.thevergov.dto.*;
import com.project.thevergov.enumeration.TokenType;
import com.project.thevergov.handler.ApiLogoutHandler;
import com.project.thevergov.service.JwtService;
import com.project.thevergov.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static com.project.thevergov.constant.Constants.FILE_STORAGE;
import static com.project.thevergov.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequestMapping(path = {"/user"})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final JwtService jwtService;

    private final ApiLogoutHandler apiLogoutHandler;

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

    @GetMapping("/verify/account")
    public ResponseEntity<Response> verifyAccount(@RequestParam("key") String key, HttpServletRequest request) {
        userService.verifyAccount(key);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account verified.", OK));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyAuthority('user:read') or hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> profile(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        User user = userService.getUserByUserId(userPrincipal.getUserId());
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "Profile retrieved", OK));
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> update(@AuthenticationPrincipal User userPrincipal, @RequestBody UserRequest userRequest, HttpServletRequest request) {
        User user = userService.updateUser(userPrincipal.getUserId(), userRequest.getFirstName(), userRequest.getLastName(), userRequest.getEmail(), userRequest.getBio());
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "User is updated successfully", OK));
    }

    @PatchMapping("/update-role")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> updateRole(@AuthenticationPrincipal User userPrincipal, @RequestBody RoleRequest roleRequest, HttpServletRequest request) {
        userService.updateRole(userPrincipal.getUserId(), roleRequest.getRole());
        return ResponseEntity.ok().body(
                getResponse(
                        request,
                        emptyMap()
                        , "Role is updated successfully",
                        OK));
    }

    @PatchMapping("/toggle-account-locked")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> toggleAccountLocked(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        userService.toggleAccountLocked(userPrincipal.getUserId());
        return ResponseEntity.ok().body(
                getResponse(
                        request,
                        emptyMap()
                        , "Account is updated successfully",
                        OK));
    }


    @PatchMapping("/toggle-account-enabled")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> toggleAccountEnabled(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        userService.toggleAccountEnabled(userPrincipal.getUserId());
        return ResponseEntity.ok().body(
                getResponse(
                        request,
                        emptyMap()
                        , "Account is updated successfully",
                        OK));
    }


    @PatchMapping("/toggle-account-expired")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> toggleAccountExpired(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        userService.toggleAccountExpired(userPrincipal.getUserId());
        return ResponseEntity.ok().body(
                getResponse(
                        request,
                        emptyMap()
                        , "Account is updated successfully",
                        OK));
    }

    @PatchMapping("/toggle-credentials-expired")
    public ResponseEntity<Response> toggleCredentialsExpired(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        userService.toggleCredentialsExpired(userPrincipal.getUserId());
        return ResponseEntity.ok().body(
                getResponse(
                        request,
                        emptyMap()
                        , "Account is updated successfully",
                        OK));
    }

    @PatchMapping("/mfa/setup")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('USER','ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> setupMfa(
            @AuthenticationPrincipal User userPrincipal,
            HttpServletRequest httpServletRequest) {

        var user = userService.setupMfa(userPrincipal.getId());

        return ResponseEntity
                .ok()
                .body(getResponse(httpServletRequest, Map.of("user", user), "MFA set up successfully", OK));
    }

    @PatchMapping("/mfa/cancel")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
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

    // start - reset password WHEN USER IS LOGGED IN
    @PatchMapping("/update-password")
    public ResponseEntity<Response> updatePassword(
            @AuthenticationPrincipal User userPrincipal,
            @RequestBody UpdatePasswordRequest passwordRequest,
            HttpServletRequest request) {
        userService.updatePassword(userPrincipal.getUserId(), passwordRequest.getPassword(), passwordRequest.getNewPassword(), passwordRequest.getConfirmNewPassword());
        return ResponseEntity.ok().body(
                getResponse(request,
                        emptyMap(),
                        "Password is updated successfully",
                        OK));
    }
    // end - reset password WHEN USER IS LOGGED IN


    // start - reset password WHEN USER IS NOT LOGGED IN
    @PostMapping("/reset-password")
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

    @PostMapping("/reset-password/reset")
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
    // end - reset password WHEN USER IS NOT LOGGED IN

    @GetMapping(path = "/list")
    @PreAuthorize("hasAnyAuthority('user:read') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> getUsers(@AuthenticationPrincipal User user, HttpServletRequest request) {
        return ResponseEntity.ok().body(getResponse(request, Map.of("users", userService.getUsers()), "Users retrieved", OK));
    }


    @PatchMapping("/photo")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> uploadPhoto(
            @AuthenticationPrincipal User userPrincipal,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        var imageUrl = userService.uploadPhoto(userPrincipal.getUserId(), file);
        return ResponseEntity.ok().body(
                getResponse(
                        request,
                        Map.of("imageUrl", imageUrl)
                        , "Photo updated successfully",
                        OK));
    }

    @PostMapping("/logout")
    public ResponseEntity<Response> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        apiLogoutHandler.logout(request, response, authentication);

        return ResponseEntity.ok().body(
                getResponse(
                        request,
                        emptyMap(),
                        "You have logged out successfully",
                        OK));
    }
    

    @GetMapping(value = "/image/{filename}", produces = {IMAGE_PNG_VALUE})
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(FILE_STORAGE + filename));

    }

    private URI getUri() {
        return URI.create("");
    }


}






























