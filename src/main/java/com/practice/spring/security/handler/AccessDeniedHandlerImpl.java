package com.practice.spring.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.spring.error.ApiErrorResponse;
import com.practice.spring.error.ErrorType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final Clock clock;

    @Autowired
    public AccessDeniedHandlerImpl(ObjectMapper objectMapper, Clock clock) {
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        HttpStatus status = HttpStatus.FORBIDDEN;
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");

        ApiErrorResponse body = new ApiErrorResponse(
                ErrorType.AUTHENTICATION_ERROR.name(),
                accessDeniedException.getMessage(),
                status.value(),
                Instant.now(clock),
                null);

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
