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


import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.project.thevergov.constant.Constants.NINETY_DAYS;

@Component
@RequiredArgsConstructor
public class ApiAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;

    private final BCryptPasswordEncoder encoder;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var apiAuthentication = authenticationFunction.apply(authentication);
        var user = userService.getUserByEmail(apiAuthentication.getEmail());
        if (user != null) {
            var userCredential = userService.getUserCredentialById(user.getId());
            if (userCredential.getUpdatedAt().minusDays(NINETY_DAYS).isAfter(LocalDateTime.now())) {
                throw new ApiException("Credentials are expired. Please reset your password");
            }
            var userPrincipal = new UserPrincipal(user, userCredential);
            validAccount.accept(userPrincipal);
            if (encoder.matches(apiAuthentication.getPassword(), userCredential.getPassword())) {
                return VergovAuthentication.authenticated(user, userPrincipal.getAuthorities());

            } else {
                throw new BadCredentialsException("Email and/or password incorrect. Please try again");
            }
        } else {
            throw new ApiException("Unable to authenticate");
        }
    }

    private final Function<Authentication, VergovAuthentication> authenticationFunction = authentication -> (VergovAuthentication) authentication;

    @Override
    public boolean supports(Class<?> authentication) {
        return VergovAuthentication.class.isAssignableFrom(authentication);
    }

    private final Consumer<UserPrincipal> validAccount = userPrincipal -> {
        if (userPrincipal.isAccountNonLocked()) {
            throw new LockedException("Your account is currently locked");
        }
        if (userPrincipal.isEnabled()) {
            throw new DisabledException("Your account is currently disabled");
        }
        if (userPrincipal.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("Your password has expired. Please update your password");
        }
        if (userPrincipal.isAccountNonLocked()) {
            throw new DisabledException("Your password has expired. Please contact administrator.");
        }

    };
}
