package com.project.thevergov.security;

import com.project.thevergov.enumeration.LoginType;
import com.project.thevergov.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

import static org.springframework.http.HttpMethod.POST;

@Slf4j
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final UserService userService;

    private final JwtService jwtService;

    protected AuthenticationFilter(
            AuthenticationManager authenticationManager,
            UserService userService,
            JwtService jwtService) {

        // this basically says to spring for what request to listen to in order to trigger the "attemptAuthentication"
        super(new AntPathRequestMatcher("/user/login", POST.name()), authenticationManager);
        this.jwtService = jwtService;
        this.userService = userService;

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
       userService.updateLoginAttempt("vergovmiroslav@gmail.com", LoginType.LOGIN_ATTEMPT);


        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
    }
}
