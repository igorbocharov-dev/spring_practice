package com.practice.spring.dto.requestEvent;

import com.practice.spring.entity.document.DocumentStatus;
import com.practice.spring.entity.requestEvent.RequestEventType;
import com.practice.spring.error.ErrorType;

import java.util.UUID;

public record RequestEventSubject (
        UUID docId,
        RequestEventType eventType,
        DocumentStatus status,
        Long processingMs,
        Integer httpStatus,
        ErrorType errorType)
{}
