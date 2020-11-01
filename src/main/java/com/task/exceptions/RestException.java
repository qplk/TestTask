package com.task.exceptions;

import org.springframework.http.HttpStatus;

public class RestException extends RuntimeException {

    private String message;

    private HttpStatus httpStatus;

    public RestException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    private RestException() {
    }

    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
