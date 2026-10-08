package com.sahana.sahanamart.exception;

import java.util.Collections;
import java.util.Map;

public class ValidationException extends AppException {
    private final Map<String, String> fieldErrors;

    public ValidationException(String message) {
        super(message, 400);
        this.fieldErrors = Collections.emptyMap();
    }

    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message, 400);
        this.fieldErrors = fieldErrors != null ? fieldErrors : Collections.emptyMap();
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
