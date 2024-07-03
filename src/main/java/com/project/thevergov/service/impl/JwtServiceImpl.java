/**
 * Implementation of {@link JwtService} that provides JWT token handling functionality.
 * This service manages token creation, extraction, cookie handling, and token data retrieval.
 */
package com.project.thevergov.service.impl;

import com.project.thevergov.domain.Token;
import com.project.thevergov.domain.TokenData;
import com.project.thevergov.dto.User;
import com.project.thevergov.enumeration.TokenType;
import com.project.thevergov.function.TriConsumer;
import com.project.thevergov.security.JwtConfiguration;
import com.project.thevergov.service.JwtService;
import com.project.thevergov.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.project.thevergov.constant.Constants.*;
import static com.project.thevergov.enumeration.TokenType.ACCESS;
import static com.project.thevergov.enumeration.TokenType.REFRESH;
import static io.jsonwebtoken.Header.JWT_TYPE;
import static io.jsonwebtoken.Header.TYPE;
import static java.util.Arrays.stream;
import static org.springframework.boot.web.server.Cookie.SameSite.NONE;
import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;

/**
 * JwtServiceImpl implements {@link JwtService} to manage JWT token operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl extends JwtConfiguration implements JwtService {

    private final UserService userService;

    // Supplier for generating HMAC secret key from base64 encoded secret
    private final Supplier<SecretKey> key = () -> Keys.hmacShaKeyFor(Decoders.BASE64.decode(getSecret()));

    // Function to parse JWT claims from token
    private final Function<String, Claims> claimsFunction = token ->
            Jwts.parser()
                    .verifyWith(key.get())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

    // Function to extract subject (user ID) from JWT token
    private final Function<String, String> subject = token -> getClaimsValue(token, Claims::getSubject);

    // BiFunction to extract JWT token from HttpServletRequest cookies by cookie name
    private final BiFunction<HttpServletRequest, String, Optional<String>> extractToken = (request, cookieName) ->
            Optional.of(stream(request.getCookies() == null ? new Cookie[]{new Cookie(EMPTY_VALUE, EMPTY_VALUE)} : request.getCookies())
                            .filter(cookie -> Objects.equals(cookieName, cookie.getName()))
                            .map(Cookie::getValue)
                            .findAny())
                    .orElse(Optional.empty());

    // BiFunction to extract Cookie object from HttpServletRequest cookies by cookie name
    private final BiFunction<HttpServletRequest, String, Optional<Cookie>> extractCookie = (request, cookieName) ->
            Optional.of(stream(request.getCookies() == null ? new Cookie[]{new Cookie(EMPTY_VALUE, EMPTY_VALUE)} : request.getCookies())
                            .filter(cookie -> Objects.equals(cookieName, cookie.getName()))
                            .findAny())
                    .orElse(Optional.empty());

    // Supplier for building JWT token with header, audience, ID, issuedAt, notBefore, and signing with secret key
    private final Supplier<JwtBuilder> builder = () ->
            Jwts.builder()
                    .header().add(Map.of(TYPE, JWT_TYPE))
                    .and()
                    .audience().add(THE_VERGOV_APP)
                    .and()
                    .id(UUID.randomUUID().toString())
                    .issuedAt(Date.from(Instant.now()))
                    .notBefore(new Date())
                    .signWith(key.get(), Jwts.SIG.HS512);

    // BiFunction to build JWT token based on user and token type (ACCESS or REFRESH)
    private final BiFunction<User, TokenType, String> buildToken = (user, type) ->
            Objects.equals(type, ACCESS) ? builder.get()
                    .subject(user.getUserId())
                    .claim(AUTHORITIES, user.getAuthorities())
                    .claim(ROLE, user.getRole())
                    .expiration(Date.from(Instant.now().plusSeconds(getExpiration())))
                    .compact() : builder.get()
                    .subject(user.getUserId())
                    .expiration(Date.from(Instant.now().plusSeconds(getExpiration())))
                    .compact();

    // TriConsumer to add JWT token as cookie to HttpServletResponse based on token type (ACCESS or REFRESH)
    private final TriConsumer<HttpServletResponse, User, TokenType> addCookie = (response, user, type) -> {
        switch (type) {
            case ACCESS -> {
                var accessToken = createToken(user, Token::getAccess);
                var cookie = new Cookie(type.getValue(), accessToken);

                cookie.setHttpOnly(true);
                //cookie.setSecure(true);
                cookie.setMaxAge(2 * 60); // 2 minutes
                cookie.setPath("/");
                cookie.setAttribute("SameSite", NONE.name());
                response.addCookie(cookie);
            }
            case REFRESH -> {
                var refreshToken = createToken(user, Token::getRefresh);
                var cookie = new Cookie(type.getValue(), refreshToken);

                cookie.setHttpOnly(true);
                //cookie.setSecure(true);
                cookie.setMaxAge(2 * 60 * 60); // 2 hours
                cookie.setPath("/");
                cookie.setAttribute("SameSite", NONE.name());
                response.addCookie(cookie);
            }
        }
    };

    // Function to extract authorities from JWT token
    public Function<String, List<GrantedAuthority>> authorities = token ->
            commaSeparatedStringToAuthorityList(new StringJoiner(AUTHORITY_DELIMITER)
                    .add(claimsFunction.apply(token).get(AUTHORITIES, String.class))
                    .add(ROLE_PREFIX + claimsFunction.apply(token).get(ROLE, String.class)).toString());

    /**
     * Creates a JWT token for the given user and token function.
     *
     * @param user         The user for whom the token is created.
     * @param tokenFunction The function to retrieve the token type (access or refresh).
     * @return The created JWT token as a String.
     */
    @Override
    public String createToken(User user, Function<Token, String> tokenFunction) {
        var token = Token.builder().access(buildToken.apply(user, ACCESS)).refresh(buildToken.apply(user, REFRESH)).build();
        return tokenFunction.apply(token);
    }

    /**
     * Extracts JWT token from HttpServletRequest cookies by cookie name.
     *
     * @param request    The HttpServletRequest object.
     * @param cookieName The name of the cookie containing the JWT token.
     * @return Optional containing the extracted JWT token as a String, or empty if not found.
     */
    @Override
    public Optional<String> extractToken(HttpServletRequest request, String cookieName) {
        return extractToken.apply(request, cookieName);
    }

    /**
     * Adds JWT token as a cookie to HttpServletResponse based on token type (access or refresh).
     *
     * @param response  The HttpServletResponse object.
     * @param user      The user for whom the token is created.
     * @param tokenType The type of token to be added (access or refresh).
     */
    @Override
    public void addCookie(HttpServletResponse response, User user, TokenType tokenType) {
        addCookie.accept(response, user, tokenType);
    }

    /**
     * Retrieves token data from JWT token using the provided token function.
     *
     * @param token        The JWT token as a String.
     * @param tokenFunction The function to retrieve specific token data.
     * @param <T>          The type of token data to retrieve.
     * @return The token data as retrieved by the tokenFunction.
     */
    @Override
    public <T> T getTokenData(String token, Function<TokenData, T> tokenFunction) {
        return tokenFunction.apply(
                TokenData.builder()
                        .valid(Objects.equals(userService.getUserByUserId(subject.apply(token)).getUserId(), claimsFunction.apply(token).getSubject()))
                        .authorities(authorities.apply(token))
                        .claims(claimsFunction.apply(token))
                        .user(userService.getUserByUserId(subject.apply(token))) //TODO: Complete the user retrieval
                        .build()
        );
    }

    // Private method to retrieve specific claim value from JWT token
    private <T> T getClaimsValue(String token, Function<Claims, T> claims) {
        return claimsFunction.andThen(claims).apply(token);
    }
}
