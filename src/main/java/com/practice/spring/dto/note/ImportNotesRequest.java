package com.practice.spring.dto.note;

import jakarta.validation.Valid;

import java.util.List;

public record ImportNotesRequest(@Valid List<CreateNoteRequest> notes) {
}
