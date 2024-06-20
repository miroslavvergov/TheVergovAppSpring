package com.project.thevergov.model.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {

    private  String email;

    private  String token;
}
