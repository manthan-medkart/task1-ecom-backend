package com.task.e_commerce.advice;

import com.task.e_commerce.exceptions.ResourceNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException exception){

        ApiError apiError = ApiError.builder()
                .message(exception.getMessage())
                .httpStatus(HttpStatus.NOT_FOUND)
                .build();

        return createErrorResponseEntity(apiError);

    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllException(@NonNull Exception exception){

        ApiError apiError = ApiError.builder()
                .message(exception.getLocalizedMessage())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();

        return createErrorResponseEntity(apiError);
    }


    public ResponseEntity<ApiError> createErrorResponseEntity(ApiError apiError){
        return new ResponseEntity<>(apiError, apiError.getHttpStatus());
    }


}
