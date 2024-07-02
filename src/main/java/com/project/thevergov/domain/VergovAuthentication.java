package com.project.thevergov.domain;

import com.project.thevergov.dto.User;
import com.project.thevergov.exception.ApiException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

public class VergovAuthentication extends AbstractAuthenticationToken {
    private static final String PASSWORD_PROTECTED = "{PASSWORD PROTECTED}";
    private static final String EMAIL_PROTECTED = "{EMAIL PROTECTED}";
    private User user;
    private String email;
    private String password;
    private Boolean authenticated;

    // we make it private in order to force the user to use the "unauthenticated"
    private VergovAuthentication(String email, String password) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.password = password;
        this.email = email;
        this.authenticated = false;
    }

    // we make it private in order to force the user to use the "authenticated"
    private VergovAuthentication(User user, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.user = user;
        this.password = PASSWORD_PROTECTED;
        this.email = EMAIL_PROTECTED;
        this.authenticated = true;
    }

    // in order to create authentication when we create one
    public static VergovAuthentication unauthenticated(String email, String password) {
        return new VergovAuthentication(email, password);
    }

    public static VergovAuthentication authenticated(User user, Collection<? extends GrantedAuthority> authorities) {
        return new VergovAuthentication(user, authorities);

    }

    @Override
    public Object getCredentials() {
        return PASSWORD_PROTECTED;
    }

    @Override
    public Object getPrincipal() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

    public String getEmail() {
        return this.email;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        throw new ApiException("You cannot set authentication");
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }
}
