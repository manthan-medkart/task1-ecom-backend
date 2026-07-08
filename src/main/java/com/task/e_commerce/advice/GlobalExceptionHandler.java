package com.task.e_commerce.advice;

import com.task.e_commerce.exceptions.ResourceNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException exception) {

        ApiError apiError = ApiError.builder()
                .message(exception.getMessage())
                .httpStatusCode(HttpStatusCode.valueOf(404))
                .error(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return createErrorResponseEntity(apiError);

    }

    @ExceptionHandler(io.jsonwebtoken.JwtException.class)
    public ResponseEntity<ApiError> handleJwtException(io.jsonwebtoken.JwtException exception) {

        ApiError apiError = ApiError.builder()
                .message("Unauthorized User")
                .httpStatusCode(HttpStatusCode.valueOf(401))
                .error(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return createErrorResponseEntity(apiError);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException exception) {
        List<String> subErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ApiError apiError = ApiError.builder()
                .message("Validation failed")
                .httpStatusCode(HttpStatusCode.valueOf(400))
                .error(subErrors)
                .timestamp(LocalDateTime.now())
                .build();

        return createErrorResponseEntity(apiError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException exception) {
        ApiError apiError = ApiError.builder()
                .message(exception.getMessage())
                .httpStatusCode(HttpStatusCode.valueOf(400))
                .error(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return createErrorResponseEntity(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllException(@NonNull Exception exception) {
        ApiError apiError = ApiError.builder()
                .message("Internal Server Occurred")
                .httpStatusCode(HttpStatusCode.valueOf(500))
                .error(exception.getLocalizedMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return createErrorResponseEntity(apiError);
    }

    public ResponseEntity<ApiError> createErrorResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getHttpStatusCode());
    }

}
