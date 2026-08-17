package com.practice.spring.mapper;

import com.practice.spring.dto.noteRevision.NoteRevisionResponse;
import com.practice.spring.entity.noteRevision.NoteRevision;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NoteRevisionMapper {

    @Mapping(target = "noteId", source = "note.id")
    NoteRevisionResponse toResponse(NoteRevision noteRevision);
}
