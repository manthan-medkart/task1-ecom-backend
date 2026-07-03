package com.task.e_commerce.advice;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponse<T> {

    private ApiError apiError;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponse(){
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(T data){
        this();
        this.data = data;
    }
    public ApiResponse(ApiError apiError){
        this();
        this.apiError = apiError;
    }

}
