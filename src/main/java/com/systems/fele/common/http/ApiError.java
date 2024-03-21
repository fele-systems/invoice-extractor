package com.systems.fele.common.http;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;

import io.micrometer.common.lang.NonNull;
import lombok.Getter;

@Getter
public class ApiError {

    @NonNull
    private HttpStatus status;
    private String message;
    private List<String> errors;

    public ApiError(HttpStatus status, String message, List<String> errors) {
        super();
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public ApiError(HttpStatus status, String message, String error) {
        super();
        this.status = status;
        this.message = message;
        errors = Arrays.asList(error);
    }
}