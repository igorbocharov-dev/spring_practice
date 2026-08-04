package com.practice.spring.service.note.exporter;

import com.practice.spring.entity.note.Note;

import java.util.List;

public interface NoteExporter {

    String export(List<Note> notes);
}
