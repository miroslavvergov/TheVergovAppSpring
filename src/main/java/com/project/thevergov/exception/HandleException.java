package com.project.thevergov.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

import static com.project.thevergov.utils.RequestUtils.handleErrorResponse;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class HandleException extends ResponseEntityExceptionHandler implements ErrorController {

    private final HttpServletRequest request;

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest webRequest) {
        log.error(String.format("handleExceptionInternal: %s", exception.getMessage()));
        return new ResponseEntity<>(handleErrorResponse(exception.getMessage(), getRootCauseMessage(exception), request, statusCode), statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode statusCode, WebRequest webRequest) {
        log.error(String.format("handleMethodArgumentNotValid: %s", exception.getMessage()));
        var fieldErrors = exception.getBindingResult().getFieldErrors();
        var fieldMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
        return new ResponseEntity<>(handleErrorResponse(fieldMessage, getRootCauseMessage(exception), request, statusCode), statusCode);
    }
}
