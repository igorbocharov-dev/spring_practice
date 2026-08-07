package com.practice.spring.exception.note;

public class ParseNoteException extends RuntimeException {
    public ParseNoteException(String message, Throwable cause) {
        super(message, cause);
    }
}
