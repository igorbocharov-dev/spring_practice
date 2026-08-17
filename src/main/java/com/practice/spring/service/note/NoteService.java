package com.practice.spring.service.note;

import com.practice.spring.dto.note.*;

public interface NoteService {

    AuthorNoteSummary authorNoteSummary(String author);

    NotesResponse findAll();

    LocationNoteResponse create(CreateNoteRequest createNoteRequest);

    NoteResponse getNoteResponseById(Long id);

    NoteResponse update(Long id, UpdateNoteRequest updateNoteRequest);

    void delete(Long id);

}
