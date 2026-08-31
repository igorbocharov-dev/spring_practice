package com.practice.spring.dto.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Обязательное поле")
        @Size(min = 3, message = "минимальное количество символов 3")
        String username,
        @NotBlank(message = "Обязательное поле")
        @Size(min = 5, message = "минимальное количество символов 5")
        String password) {
}
