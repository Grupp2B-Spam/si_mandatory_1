package com.example.librarysoap.fault;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
