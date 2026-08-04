package com.practice.spring.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        String errorCode,
        String message,
        int status,
        Instant currentTime,
        Map<String, List<String>> errors
) {}