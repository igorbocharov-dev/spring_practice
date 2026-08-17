package com.practice.spring.service.noteRevision;

import com.practice.spring.dto.noteRevision.NoteRevisionResponse;
import com.practice.spring.dto.paging.SliceResponse;
import com.practice.spring.entity.note.Note;

public interface NoteRevisionService {

    void save(Note note);

    SliceResponse<NoteRevisionResponse> getAllHistory(int page, int size);
}
