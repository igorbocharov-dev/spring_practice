package com.practice.spring.dto.note;

import java.util.List;

public record NotesResponse(List<NoteResponse> notes) {
}
