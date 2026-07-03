package com.task.e_commerce.advice;

import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ApiError {

    private String message;
    private HttpStatus httpStatus;


}
