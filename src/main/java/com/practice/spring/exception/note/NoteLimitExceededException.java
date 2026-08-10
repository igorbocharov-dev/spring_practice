package com.practice.spring.exception.note;

public class NoteLimitExceededException extends RuntimeException {
    public NoteLimitExceededException(String message) {
        super(message);
    }
}
