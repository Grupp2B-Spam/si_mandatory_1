package com.example.librarysoap.fault;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
