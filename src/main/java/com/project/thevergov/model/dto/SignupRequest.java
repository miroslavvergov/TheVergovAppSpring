package com.project.thevergov.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Objects;


@Data
public class SignupRequest {


    @NotBlank(message = "Username cannot be blank")
    private String username;
     @Email(message = "Invalid email format")
     @NotBlank(message = "Email cannot be blank")
     private String email;

     @NotBlank(message = "Password cannot be blank")
     @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
     private String password;


}
