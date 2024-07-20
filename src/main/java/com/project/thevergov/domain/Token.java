package com.project.thevergov.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Token: A class representing a pair of access and refresh tokens.
 * This class uses Lombok annotations to generate boilerplate code such as getters, setters, and a builder.
 */
@Builder
@Getter
@Setter
public class Token {

    // Access token used for authenticating API requests
    private String access;

    // Refresh token used to obtain a new access token when the current one expires
    private String refresh;
}
