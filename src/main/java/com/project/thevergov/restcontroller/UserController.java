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
import java.util.concurrent.TimeUnit;

import static com.project.thevergov.constant.Constants.FILE_STORAGE;
import static com.project.thevergov.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

/**
 * UserController: Manages user-related operations such as registration, profile management,
 * password reset, multi-factor authentication (MFA), and photo management.
 * <p>
 * This controller provides endpoints for users to register, verify accounts, update profiles,
 * reset passwords, manage MFA, and upload photos. Access to these endpoints is controlled
 * through user roles and permissions.
 */
@RestController
@RequestMapping(path = {"/user"})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final ApiLogoutHandler apiLogoutHandler;

    /**
     * Registers a new user.
     * <p>
     * This endpoint allows users to register by providing their personal details. After successful
     * registration, an email is sent to the user for account activation.
     *
     * @param user    the request body containing user details
     * @param request the HTTP request object for generating the response
     * @return a response entity indicating the account creation status
     */
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

    /**
     * Verifies a user's account using a verification key.
     * <p>
     * This endpoint is used to verify a user's account after registration by providing a verification key.
     *
     * @param key     the verification key sent to the user's email
     * @param request the HTTP request object for generating the response
     * @return a response entity indicating the account verification status
     * @throws InterruptedException if the thread is interrupted during sleep
     */
    @GetMapping("/verify/account")
    public ResponseEntity<Response> verifyAccount(@RequestParam("key") String key, HttpServletRequest request) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        userService.verifyAccount(key);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account verified.", OK));
    }

    /**
     * Retrieves the profile of the authenticated user.
     * <p>
     * This endpoint returns the profile information of the currently authenticated user.
     *
     * @param userPrincipal the authenticated user
     * @param request       the HTTP request object for generating the response
     * @return a response entity containing the user's profile information
     */
    @GetMapping("/profile")
    @PreAuthorize("hasAnyAuthority('user:read') or hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> profile(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        User user = userService.getUserByUserId(userPrincipal.getUserId());
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "Profile retrieved", OK));
    }

    /**
     * Updates the profile of the authenticated user.
     * <p>
     * This endpoint allows the authenticated user to update their profile details such as name, email, and bio.
     *
     * @param userPrincipal the authenticated user
     * @param userRequest   the request body containing updated user details
     * @param request       the HTTP request object for generating the response
     * @return a response entity indicating the update status
     */
    @PatchMapping("/update")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> update(@AuthenticationPrincipal User userPrincipal, @RequestBody UserRequest userRequest, HttpServletRequest request) {
        User user = userService.updateUser(userPrincipal.getUserId(), userRequest.getFirstName(), userRequest.getLastName(), userRequest.getEmail(), userRequest.getBio());
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "User is updated successfully", OK));
    }

    /**
     * Updates the role of the authenticated user.
     * <p>
     * This endpoint allows the authenticated user to update their role.
     *
     * @param userPrincipal the authenticated user
     * @param roleRequest   the request body containing the new role
     * @param request       the HTTP request object for generating the response
     * @return a response entity indicating the role update status
     */
    @PatchMapping("/update-role")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> updateRole(@AuthenticationPrincipal User userPrincipal, @RequestBody RoleRequest roleRequest, HttpServletRequest request) {
        userService.updateRole(userPrincipal.getUserId(), roleRequest.getRole());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Role is updated successfully", OK));
    }

    /**
     * Toggles the locked status of the authenticated user account.
     * <p>
     * This endpoint allows toggling the locked status of the user's account.
     *
     * @param userPrincipal the authenticated user
     * @param request       the HTTP request object for generating the response
     * @return a response entity indicating the account locked status update
     */
    @PatchMapping("/toggle-account-locked")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> toggleAccountLocked(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        userService.toggleAccountLocked(userPrincipal.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account is updated successfully", OK));
    }

    /**
     * Toggles the enabled status of the authenticated user account.
     * <p>
     * This endpoint allows toggling the enabled status of the user's account.
     *
     * @param userPrincipal the authenticated user
     * @param request       the HTTP request object for generating the response
     * @return a response entity indicating the account enabled status update
     */
    @PatchMapping("/toggle-account-enabled")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> toggleAccountEnabled(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        userService.toggleAccountEnabled(userPrincipal.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account is updated successfully", OK));
    }

    /**
     * Toggles the expired status of the authenticated user account.
     * <p>
     * This endpoint allows toggling the expired status of the user's account.
     *
     * @param userPrincipal the authenticated user
     * @param request       the HTTP request object for generating the response
     * @return a response entity indicating the account expired status update
     */
    @PatchMapping("/toggle-account-expired")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> toggleAccountExpired(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        userService.toggleAccountExpired(userPrincipal.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account is updated successfully", OK));
    }

    /**
     * Toggles the expired status of the authenticated user's credentials.
     * <p>
     * This endpoint allows toggling the expired status of the user's credentials.
     *
     * @param userPrincipal the authenticated user
     * @param request       the HTTP request object for generating the response
     * @return a response entity indicating the credentials expired status update
     */
    @PatchMapping("/toggle-credentials-expired")
    public ResponseEntity<Response> toggleCredentialsExpired(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        userService.toggleCredentialsExpired(userPrincipal.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account is updated successfully", OK));
    }

    /**
     * Sets up Multi-Factor Authentication (MFA) for the authenticated user.
     * <p>
     * This endpoint allows the user to set up MFA by generating the required configurations.
     *
     * @param userPrincipal the authenticated user
     * @param httpServletRequest the HTTP request object for generating the response
     * @return a response entity indicating the MFA setup status
     */
    @PatchMapping("/mfa/setup")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('USER','ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> setupMfa(@AuthenticationPrincipal User userPrincipal, HttpServletRequest httpServletRequest) {
        var user = userService.setupMfa(userPrincipal.getId());
        return ResponseEntity.ok().body(getResponse(httpServletRequest, Map.of("user", user), "MFA set up successfully", OK));
    }

    /**
     * Cancels Multi-Factor Authentication (MFA) for the authenticated user.
     * <p>
     * This endpoint allows the user to cancel MFA if they no longer wish to use it.
     *
     * @param userPrincipal the authenticated user
     * @param httpServletRequest the HTTP request object for generating the response
     * @return a response entity indicating the MFA cancellation status
     */
    @PatchMapping("/mfa/cancel")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> cancelMfa(@AuthenticationPrincipal User userPrincipal, HttpServletRequest httpServletRequest) {
        var user = userService.cancelMfa(userPrincipal.getId());
        return ResponseEntity.ok().body(getResponse(httpServletRequest, Map.of("user", user), "MFA canceled successfully", OK));
    }

    /**
     * Verifies a QR code for Multi-Factor Authentication (MFA).
     * <p>
     * This endpoint verifies a QR code used to set up MFA for the user. If successful, JWT tokens are issued.
     *
     * @param qrCodeRequest the request body containing the user ID and QR code
     * @param response      the HTTP response object for setting cookies
     * @param request       the HTTP request object for generating the response
     * @return a response entity indicating the QR code verification status
     */
    @PatchMapping("/verify/qrcode")
    public ResponseEntity<Response> verifyQrcode(@RequestBody QrCodeRequest qrCodeRequest, HttpServletResponse response, HttpServletRequest request) {
        var user = userService.verifyQrCode(qrCodeRequest.getUserId(), qrCodeRequest.getQrCode());
        jwtService.addCookie(response, user, TokenType.ACCESS);
        jwtService.addCookie(response, user, TokenType.REFRESH);
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "QR code verified", OK));
    }

    /**
     * Updates the password of the authenticated user.
     * <p>
     * This endpoint allows the authenticated user to update their password.
     *
     * @param userPrincipal the authenticated user
     * @param passwordRequest the request body containing the current and new passwords
     * @param request       the HTTP request object for generating the response
     * @return a response entity indicating the password update status
     */
    @PatchMapping("/update-password")
    public ResponseEntity<Response> updatePassword(@AuthenticationPrincipal User userPrincipal, @RequestBody UpdatePasswordRequest passwordRequest, HttpServletRequest request) {
        userService.updatePassword(userPrincipal.getUserId(), passwordRequest.getPassword(), passwordRequest.getNewPassword(), passwordRequest.getConfirmNewPassword());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Password is updated successfully", OK));
    }

    /**
     * Requests a password reset email for the user.
     * <p>
     * This endpoint sends a password reset email to the provided email address.
     *
     * @param emailRequest the request body containing the user's email address
     * @param request      the HTTP request object for generating the response
     * @return a response entity indicating the status of the password reset request
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Response> resetPassword(@RequestBody @Valid EmailRequest emailRequest, HttpServletRequest request) {
        userService.resetPassword(emailRequest.getEmail());
        return ResponseEntity.created(getUri())
                .body(getResponse(request, emptyMap(), "An email has been sent to reset your password.", OK));
    }

    /**
     * Verifies the password reset key.
     * <p>
     * This endpoint verifies the key provided in the password reset email.
     *
     * @param key     the password reset key
     * @param request the HTTP request object for generating the response
     * @return a response entity indicating the status of the password reset key verification
     */
    @GetMapping("/verify/password")
    public ResponseEntity<Response> verifyPasswordKey(@RequestParam("key") String key, HttpServletRequest request) {
        var user = userService.verifyPasswordKey(key);
        return ResponseEntity.created(getUri())
                .body(getResponse(request, Map.of("user", user), "Enter new password.", OK));
    }

    /**
     * Resets the user's password.
     * <p>
     * This endpoint allows the user to set a new password after verifying the reset key.
     *
     * @param resetPasswordRequest the request body containing the new password details
     * @param request              the HTTP request object for generating the response
     * @return a response entity indicating the status of the password reset
     */
    @PostMapping("/reset-password/reset")
    public ResponseEntity<Response> doResetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest, HttpServletRequest request) {
        userService.updatePassword(resetPasswordRequest.getUserId(), resetPasswordRequest.getNewPassword(), resetPasswordRequest.getConfirmNewPassword());
        return ResponseEntity.created(getUri())
                .body(getResponse(request, emptyMap(), "Password reset successfully", OK));
    }

    /**
     * Retrieves a list of all users.
     * <p>
     * This endpoint returns a list of all users in the system.
     *
     * @param user    the authenticated user (used for authorization purposes)
     * @param request the HTTP request object for generating the response
     * @return a response entity containing the list of users
     */
    @GetMapping(path = "/list")
    @PreAuthorize("hasAnyAuthority('user:read') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> getUsers(@AuthenticationPrincipal User user, HttpServletRequest request) {
        return ResponseEntity.ok().body(getResponse(request, Map.of("users", userService.getUsers()), "Users retrieved", OK));
    }

    /**
     * Uploads a new profile photo for the authenticated user.
     * <p>
     * This endpoint allows the authenticated user to upload a new profile photo.
     *
     * @param userPrincipal the authenticated user
     * @param file          the profile photo to be uploaded
     * @param request       the HTTP request object for generating the response
     * @return a response entity indicating the photo upload status
     */
    @PatchMapping("/photo")
    @PreAuthorize("hasAnyAuthority('user:update') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Response> uploadPhoto(@AuthenticationPrincipal User userPrincipal, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        var imageUrl = userService.uploadPhoto(userPrincipal.getUserId(), file);
        return ResponseEntity.ok().body(getResponse(request, Map.of("imageUrl", imageUrl), "Photo updated successfully", OK));
    }

    /**
     * Logs out the authenticated user.
     * <p>
     * This endpoint handles the logout process and invalidates the user's session.
     *
     * @param request        the HTTP request object
     * @param response       the HTTP response object
     * @param authentication the authentication object for the current session
     * @return a response entity indicating the logout status
     */
    @PostMapping("/logout")
    public ResponseEntity<Response> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        apiLogoutHandler.logout(request, response, authentication);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "You have logged out successfully", OK));
    }

    /**
     * Retrieves a user's photo.
     * <p>
     * This endpoint serves the profile photo of a user given the filename.
     *
     * @param filename the filename of the photo
     * @return the photo as a byte array
     * @throws IOException if the photo file cannot be read
     */
    @GetMapping(value = "/image/{filename}", produces = {IMAGE_PNG_VALUE})
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(FILE_STORAGE + filename));
    }

    /**
     * Generates a URI for the response headers.
     * <p>
     * This method is used for creating the URI in response headers for creation operations.
     *
     * @return a URI object
     */
    private URI getUri() {
        return URI.create("");
    }
}
