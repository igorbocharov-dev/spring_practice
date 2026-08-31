package com.practice.spring.exception.note;

public class ImportNoteException extends RuntimeException {
    public ImportNoteException(String message) {
        super(message);
    }
}
