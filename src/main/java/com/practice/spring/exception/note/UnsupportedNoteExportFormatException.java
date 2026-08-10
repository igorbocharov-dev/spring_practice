package com.practice.spring.exception.note;

public class UnsupportedNoteExportFormatException extends RuntimeException {
    public UnsupportedNoteExportFormatException(String message) {
        super(message);
    }
}
