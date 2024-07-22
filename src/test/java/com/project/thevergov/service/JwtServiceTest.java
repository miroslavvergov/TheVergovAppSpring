package com.project.thevergov.service;

import com.project.thevergov.domain.Token;
import com.project.thevergov.domain.TokenData;
import com.project.thevergov.dto.User;
import com.project.thevergov.enumeration.TokenType;
import com.project.thevergov.service.UserService;
import com.project.thevergov.service.impl.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.project.thevergov.enumeration.TokenType.ACCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;
    @InjectMocks
    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService = new JwtServiceImpl(userService);
    }

    @Test
    void extractToken_shouldReturnTokenWhenCookieExists() {
        Cookie[] cookies = {new Cookie("accessToken", "testToken")};
        when(request.getCookies()).thenReturn(cookies);

        Optional<String> token = jwtService.extractToken(request, "accessToken");

        assertThat(token).isPresent();
        assertThat(token.get()).isEqualTo("testToken");
    }

    @Test
    void extractToken_shouldReturnEmptyWhenCookieDoesNotExist() {
        when(request.getCookies()).thenReturn(null);

        Optional<String> token = jwtService.extractToken(request, "accessToken");

        assertThat(token).isNotPresent();
    }

    @Test
    void removeCookie_shouldRemoveCookie() {
        Cookie cookie = new Cookie("accessToken", "testToken");
        Cookie[] cookies = {cookie};
        when(request.getCookies()).thenReturn(cookies);

        jwtService.removeCookie(request, response, "accessToken");

        verify(response, times(1)).addCookie(any(Cookie.class));
        assertThat(cookie.getMaxAge()).isZero();
    }
}
