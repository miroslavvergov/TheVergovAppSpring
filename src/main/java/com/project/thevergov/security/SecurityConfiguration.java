package com.project.thevergov.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static com.project.thevergov.constant.Constants.STRENGTH;

@Configuration
public class SecurityConfiguration {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        // i do 12 just in preference
        return new BCryptPasswordEncoder(STRENGTH);
    }
}
