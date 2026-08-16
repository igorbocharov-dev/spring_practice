package com.practice.spring.dto.note;

import java.util.List;

public record ImportNotesRequest(List<CreateNoteRequest> notes) {
}
