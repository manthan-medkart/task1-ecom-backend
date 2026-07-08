package com.task.e_commerce.advice;

import lombok.*;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    private HttpStatusCode httpStatusCode;
    private String message;
    private Object error;
    private LocalDateTime timestamp;

}
