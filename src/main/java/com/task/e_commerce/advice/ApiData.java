package com.task.e_commerce.advice;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

@Data
public class ApiData<T> {

    private HttpStatusCode httpStatusCode;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public ApiData() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiData(HttpStatusCode httpStatus, String message, T data) {
        this();
        this.httpStatusCode = httpStatus;
        this.message = message;
        this.data = data;
    }
}
