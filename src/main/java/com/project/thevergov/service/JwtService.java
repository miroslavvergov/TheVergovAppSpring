package com.project.thevergov.service;


import com.project.thevergov.domain.Token;
import com.project.thevergov.domain.TokenData;
import com.project.thevergov.dto.User;
import com.project.thevergov.enumeration.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.function.Function;

public interface JwtService {

    String createToken(User user, Function<Token, String> tokenFunction);

    Optional<String> extractToken(HttpServletRequest request, String tokenType);

    void addCookie(HttpServletResponse response, User user, TokenType tokenType);

    <T> T getTokenData(String token, Function<TokenData, T> tokenFunction);
}
