package com.practice.spring.dto.document;

import com.practice.spring.entity.document.DocumentStatus;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DocumentResponse(
        UUID id,
        String body,
        List<String> links,
        DocumentStatus status,
        Instant createdAt,
        Instant updatedAt) {
}
