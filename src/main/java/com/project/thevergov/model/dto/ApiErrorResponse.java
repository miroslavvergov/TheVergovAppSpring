package com.project.thevergov.model.dto;


public record ApiErrorResponse(
       int errorCode,
        String description) {}
