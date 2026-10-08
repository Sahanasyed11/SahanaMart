package com.sahana.sahanamart.exception;

public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(String message) {
        super(message, 404);
    }
}
