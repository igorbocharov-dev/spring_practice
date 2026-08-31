package com.practice.spring.service.note.importer;

import com.practice.spring.dto.note.ImportNotesRequest;

public interface ImportNoteService {
    void importNotes(ImportNotesRequest request);
}
