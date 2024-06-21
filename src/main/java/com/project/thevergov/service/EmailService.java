package com.project.thevergov.service;

public interface EmailService {

    void sendNewAccountEmail(String name, String email, String token);
    //Just an idea still //TODO
    void sendPasswordResetEmail(String name, String email, String token);
}
