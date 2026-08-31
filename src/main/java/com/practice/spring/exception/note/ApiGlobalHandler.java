package com.practice.spring.exception.note;

import com.practice.spring.error.ApiErrorResponse;
import com.practice.spring.error.ErrorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Clock;
import java.time.Instant;

@RestControllerAdvice
public class ApiGlobalHandler {

    private final Clock clock;

    @Autowired
    public ApiGlobalHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> noteNotFoundHandle(NoteNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(
                        ErrorType.NOTE_NOT_FOUND_ERROR.name(),
                        e.getMessage(),
                        HttpStatus.NOT_FOUND.value(),
                        Instant.now(clock), null));
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> authorNotFoundHandle(AuthorNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(
                        ErrorType.AUTHOR_NOT_FOUND_ERROR.name(),
                        e.getMessage(),
                        HttpStatus.NOT_FOUND.value(),
                        Instant.now(clock), null));
    }

    @ExceptionHandler(NoteLimitExceededException.class)
    public ResponseEntity<ApiErrorResponse> noteLimitHandle(NoteLimitExceededException e){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(
                        ErrorType.NOTE_LIMIT_ERROR.name(),
                        e.getMessage(),
                        HttpStatus.CONFLICT.value(),
                        Instant.now(clock), null));
    }

    @ExceptionHandler(UnsupportedNoteExportFormatException.class)
    public ResponseEntity<ApiErrorResponse> unsupportedExportFormatHandle(UnsupportedNoteExportFormatException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(
                        ErrorType.UNSUPPORTED_EXPORT_NOTE_FORMAT_ERROR.name(),
                        e.getMessage(),
                        HttpStatus.BAD_REQUEST.value(),
                        Instant.now(clock), null));
    }

    @ExceptionHandler(IllegalNoteExportFormatException.class)
    public ResponseEntity<ApiErrorResponse> IllegalExportFormatHandle(IllegalNoteExportFormatException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(
                        ErrorType.ILLEGAL_EXPORT_NOTE_FORMAT_ERROR.name(),
                        e.getMessage(),
                        HttpStatus.BAD_REQUEST.value(),
                        Instant.now(clock), null));
    }

    @ExceptionHandler(ParseNoteException.class)
    public ResponseEntity<ApiErrorResponse> parseNotesHandle(ParseNoteException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(
                        ErrorType.PARSE_NOTE_ERROR.name(),
                        e.getMessage(),
                        HttpStatus.BAD_REQUEST.value(),
                        Instant.now(clock), null));
    }

    @ExceptionHandler(ImportNoteException.class)
    public ResponseEntity<ApiErrorResponse> importNoteHandle(ImportNoteException e){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(
                        ErrorType.IMPORT_NOTES_ERROR.name(),
                        e.getMessage(),
                        HttpStatus.CONFLICT.value(),
                        Instant.now(clock), null));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiErrorResponse> objectOptimisticLockingFailureHandle(ObjectOptimisticLockingFailureException e){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(
                        ErrorType.OPTIMISTIC_LOCKING_ERROR.name(),
                        e.getMessage(),
                        HttpStatus.CONFLICT.value(),
                        Instant.now(clock), null));
    }
}
