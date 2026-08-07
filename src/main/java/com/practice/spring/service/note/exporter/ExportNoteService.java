package com.practice.spring.service.note.exporter;

import com.practice.spring.dto.note.ExportNotesResult;
import com.practice.spring.entity.note.Note;
import com.practice.spring.repository.note.NoteRepository;
import com.practice.spring.util.validator.note.NotesValidator;
import com.practice.spring.util.validator.note.exporter.ExportNoteFormatValidator;
import com.practice.spring.util.validator.note.exporter.NoteExporterValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ExportNoteService {

    private final Map<String, NoteExporter> exporters;
    private final NoteExporterValidator noteExporterValidator;
    private final ExportNoteFormatValidator exportNoteFormatValidator;
    private final NotesValidator notesValidator;
    private final NoteRepository noteRepository;

    @Autowired
    public ExportNoteService(Map<String, NoteExporter> exporters,
                             NoteExporterValidator noteExporterValidator,
                             ExportNoteFormatValidator exportNoteFormatValidator,
                             NotesValidator notesValidator,
                             @Qualifier(value = "NoteRepository") NoteRepository noteRepository) {
        this.exporters = exporters;
        this.noteExporterValidator = noteExporterValidator;
        this.exportNoteFormatValidator = exportNoteFormatValidator;
        this.notesValidator = notesValidator;
        this.noteRepository = noteRepository;
    }

    public ExportNotesResult export(String format){
        exportNoteFormatValidator.validate(format);
        NoteExporter exporter = exporters.get(format);
        noteExporterValidator.validate(exporter);
        List<Note> notes = noteRepository.findAll();
        notesValidator.validate(notes);
        return exporter.export(notes);
    }
}
