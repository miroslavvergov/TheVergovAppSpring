package com.project.thevergov.domain.dto;


public record ApiErrorResponse(
       int errorCode,
        String description) {}
