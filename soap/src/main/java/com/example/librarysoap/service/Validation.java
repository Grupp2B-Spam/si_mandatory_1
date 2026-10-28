package com.example.librarysoap.service;

import com.example.librarysoap.fault.ValidationException;

final class Validation {
    private Validation() {}

    static void requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(field + " is required");
        }
        if (value.length() > maxLength) {
            throw new ValidationException(field + " must be at most " + maxLength + " characters");
        }
    }
}
