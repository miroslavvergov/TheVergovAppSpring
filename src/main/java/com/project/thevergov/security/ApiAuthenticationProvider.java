package com.project.thevergov.security;

import com.project.thevergov.domain.UserPrincipal;
import com.project.thevergov.domain.VergovAuthentication;
import com.project.thevergov.exception.ApiException;
import com.project.thevergov.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.project.thevergov.domain.VergovAuthentication.authenticated;

/**
 * ApiAuthenticationProvider: Custom implementation of AuthenticationProvider.
 * <p>
 * This provider handles the authentication logic, including checking credentials,
 * account status, and more. It uses BCrypt for password encoding and validation.
 */
@Component
@RequiredArgsConstructor
public class ApiAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final BCryptPasswordEncoder encoder;

    /**
     * Authenticates the user by verifying their credentials and account status.
     *
     * @param authentication the authentication request object containing credentials
     * @return an authenticated Authentication object if credentials are valid
     * @throws AuthenticationException if authentication fails
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // Convert Authentication to VergovAuthentication
        var apiAuthentication = authenticationFunction.apply(authentication);

        // Retrieve the user by email
        var user = userService.getUserByEmail(apiAuthentication.getEmail());

        if (user != null) {
            // Retrieve user credentials
            var userCredential = userService.getUserCredentialById(user.getId());

            // Check if the credentials are expired
            if (!user.isCredentialsNonExpired()) {
                throw new ApiException("Credentials are expired. Please reset your password");
            }

            var userPrincipal = new UserPrincipal(user, userCredential);

            // Validate the account status
            validAccount.accept(userPrincipal);

            // Verify the password
            if (encoder.matches(apiAuthentication.getPassword(), userCredential.getPassword())) {
                return authenticated(user, userPrincipal.getAuthorities());
            } else {
                throw new BadCredentialsException("Email and/or password incorrect. Please try again");
            }
        }

        throw new ApiException("Unable to authenticate");
    }

    // Function to convert Authentication to VergovAuthentication
    private final Function<Authentication, VergovAuthentication> authenticationFunction = authentication ->
            (VergovAuthentication) authentication;

    /**
     * Checks if the authentication class is supported.
     *
     * @param authentication the class to check
     * @return true if the class is assignable from VergovAuthentication, false otherwise
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return VergovAuthentication.class.isAssignableFrom(authentication);
    }

    // Consumer to validate the account status
    private final Consumer<UserPrincipal> validAccount = userPrincipal -> {
        if (!userPrincipal.isAccountNonLocked()) {
            throw new LockedException("Your account is currently locked");
        }
        if (!userPrincipal.isEnabled()) {
            throw new DisabledException("Your account is currently disabled");
        }
        if (!userPrincipal.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("Your password has expired. Please update your password");
        }
    };
}
