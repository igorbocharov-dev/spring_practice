package com.practice.spring.service.note.exporter;

import com.practice.spring.dto.note.ExportNotesResult;
import com.practice.spring.entity.note.Note;

import java.util.List;

public interface NoteExporter {
    ExportNotesResult export(List<Note> notes);
}
