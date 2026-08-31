package com.practice.spring.mapper;

import com.practice.spring.dto.note.CreateNoteRequest;
import com.practice.spring.dto.note.NoteResponse;
import com.practice.spring.entity.note.Note;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    NoteResponse toNoteResponse(Note note);

    Note toEntity(String author, CreateNoteRequest createNoteRequest);
}
