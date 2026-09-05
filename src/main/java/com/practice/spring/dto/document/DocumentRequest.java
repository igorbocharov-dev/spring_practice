package com.practice.spring.dto.document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record DocumentRequest(
        @NotBlank(message = "тело не должно быть пустым")
        @Size(min = 5, message = "минимальное количество символов 5")
        String body,

        List<String> links) {
}
