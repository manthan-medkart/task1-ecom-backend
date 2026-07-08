package com.task.e_commerce.advice;

import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public @Nullable Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType,
            MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response) {
        
        if (body instanceof ApiData<?> || body instanceof ApiError) {
            return body;
        }

        // Determine HTTP Status dynamically from the response
        HttpStatus status = HttpStatus.OK;
        if (response instanceof org.springframework.http.server.ServletServerHttpResponse servletResponse) {
            int statusCode = servletResponse.getServletResponse().getStatus();
            try {
                status = HttpStatus.valueOf(statusCode);
            } catch (IllegalArgumentException e) {
                // Keep default OK if status code is non-standard
            }
        }

        // Wrap success response in ApiData
        return new ApiData<>(status, "Request completed successfully", body);
    }
}
