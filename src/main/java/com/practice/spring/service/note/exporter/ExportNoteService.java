package com.practice.spring.service.note.exporter;

import com.practice.spring.service.note.NoteService;
import com.practice.spring.util.validator.note.exporter.ExportNoteFormatValidator;
import com.practice.spring.util.validator.note.exporter.NoteExporterValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ExportNoteService {

    private final Map<String, NoteExporter> exporters;
    private final NoteExporterValidator noteExporterValidator;
    private final ExportNoteFormatValidator exportNoteFormatValidator;
    private final NoteService noteService;

    @Autowired
    public ExportNoteService(Map<String, NoteExporter> exporters, NoteExporterValidator noteExporterValidator, ExportNoteFormatValidator exportNoteFormatValidator, NoteService noteService) {
        this.exporters = exporters;
        this.noteExporterValidator = noteExporterValidator;
        this.exportNoteFormatValidator = exportNoteFormatValidator;
        this.noteService = noteService;
    }

    public String export(String format){
        exportNoteFormatValidator.validate(format);
        NoteExporter exporter = exporters.get(format);
        noteExporterValidator.validate(exporter);
        return exporter.export(noteService.findAll());
    }
}
