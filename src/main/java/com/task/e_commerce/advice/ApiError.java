package com.task.e_commerce.advice;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    private HttpStatus httpStatus;
    private String message;
    private Object error;
    private LocalDateTime timestamp;

}
