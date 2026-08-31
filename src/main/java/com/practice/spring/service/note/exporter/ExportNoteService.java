package com.practice.spring.service.note.exporter;

import com.practice.spring.dto.note.ExportNotesResult;

public interface ExportNoteService {
    ExportNotesResult export(String format);
}
