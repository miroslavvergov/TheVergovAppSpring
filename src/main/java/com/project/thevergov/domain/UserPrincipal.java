package com.project.thevergov.domain;

import com.project.thevergov.dto.User;
import com.project.thevergov.entity.CredentialEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * UserPrincipal: A class that implements UserDetails to provide authentication and authorization information.
 * This class bridges the application's user and credential entities with Spring Security's UserDetails interface.
 */
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {

    // User object containing user details
    @Getter
    private final User user;

    // CredentialEntity object containing user's credentials
    private final CredentialEntity credentialEntity;

    /**
     * Retrieves the authorities granted to the user.
     *
     * @return a collection of GrantedAuthority objects representing the user's authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList(user.getAuthorities());
    }

    /**
     * Retrieves the password used to authenticate the user.
     *
     * @return the user's password
     */
    @Override
    public String getPassword() {
        return credentialEntity.getPassword();
    }

    /**
     * Retrieves the username used to authenticate the user.
     *
     * @return the user's email
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return true if the user's account is non-expired, false otherwise
     */
    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    /**
     * Indicates whether the user is locked or unlocked.
     *
     * @return true if the user's account is non-locked, false otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    /**
     * Indicates whether the user's credentials (password) have expired.
     *
     * @return true if the user's credentials are non-expired, false otherwise
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return user.isCredentialsNonExpired();
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return true if the user is enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
