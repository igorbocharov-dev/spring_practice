package com.practice.spring.service.note;

import com.practice.spring.dto.note.*;

public interface NoteService {

    AuthorNoteSummary authorNoteSummary(String author);

    NotesResponse findAll();

    LocationNoteResponse create(String author, CreateNoteRequest createNoteRequest);

    NoteResponse getNoteResponseById(Long id);

    NoteResponse update(Long id, String author, UpdateNoteRequest updateNoteRequest);

    void delete(Long id, String author);

}
