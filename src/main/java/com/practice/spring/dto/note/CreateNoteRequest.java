package com.practice.spring.dto.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateNoteRequest
        (
        @NotBlank(message = "заголовок не должен быть пустым")
        @Size(min = 5, max = 250, message = "минимальное количество символов 5")
        String title,
        @NotBlank(message = "тело заметки не должно быть пустым")
        @Size(min = 1, message = "минимальное количество символов 1")
        String text,
        @NotBlank(message = "имя автора не должно быть пустым")
        String author
        )
{}