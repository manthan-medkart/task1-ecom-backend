package com.task.e_commerce.advice;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ApiData<T> {

    private HttpStatus httpStatus;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public ApiData() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiData(HttpStatus httpStatus, String message, T data) {
        this();
        this.httpStatus = httpStatus;
        this.message = message;
        this.data = data;
    }
}
