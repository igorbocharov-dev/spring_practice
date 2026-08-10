package com.practice.spring.service.note.exporter;

import com.practice.spring.dto.note.ExportNotesResult;
import com.practice.spring.entity.note.Note;
import org.springframework.http.MediaType;

import java.util.List;

public interface NoteExporter {
    ExportNotesResult export(List<Note> notes);
}
