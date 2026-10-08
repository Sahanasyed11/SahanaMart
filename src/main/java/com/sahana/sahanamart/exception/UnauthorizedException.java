package com.sahana.sahanamart.exception;

public class UnauthorizedException extends AppException {
    public UnauthorizedException(String message) {
        super(message, 403);
    }
}
