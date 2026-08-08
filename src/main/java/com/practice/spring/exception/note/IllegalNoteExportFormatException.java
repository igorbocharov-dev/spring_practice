package com.practice.spring.exception.note;

public class IllegalNoteExportFormatException extends RuntimeException {
    public IllegalNoteExportFormatException(String message) {
        super(message);
    }
}
