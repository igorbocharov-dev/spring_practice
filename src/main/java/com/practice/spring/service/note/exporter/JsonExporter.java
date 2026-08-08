package com.practice.spring.service.note.exporter;

import com.practice.spring.dto.note.ExportNotesResult;
import com.practice.spring.entity.note.Note;
import com.practice.spring.exception.note.ParseNoteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component(value = "JSON")
public class JsonExporter implements NoteExporter{

    private final ObjectMapper objectMapper;

    @Autowired
    public JsonExporter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ExportNotesResult export(List<Note> notes) {
        final byte [] data;
        try {
            data = objectMapper.writeValueAsBytes(notes);
        } catch (JacksonException e) {
            throw new ParseNoteException("Ошибка при записи значения 'notes' в JSON", e);
        }
        return new ExportNotesResult(data, MediaType.APPLICATION_JSON);
    }
}
