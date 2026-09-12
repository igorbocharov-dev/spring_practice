package com.practice.spring.entity.requestEvent;

import com.practice.spring.entity.document.DocumentStatus;
import com.practice.spring.error.ErrorType;

import java.time.Instant;
import java.util.UUID;

public record RequestEvent (
        Instant eventTime,
        UUID docId,
        RequestEventType requestEventType,
        DocumentStatus status,
        Long processingMs,
        Integer httpStatus,
        ErrorType errorType
) {}