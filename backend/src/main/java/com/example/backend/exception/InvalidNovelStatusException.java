package com.example.backend.exception;

public class InvalidNovelStatusException extends RuntimeException {
    public InvalidNovelStatusException(String message) {
        super("Unknown novel status: " + message);
    }
}

