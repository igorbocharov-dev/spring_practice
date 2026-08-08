package com.practice.spring.unit.service.note.exporter;

import com.practice.spring.dto.note.ExportNotesResult;
import com.practice.spring.entity.note.Note;
import com.practice.spring.service.note.exporter.CsvExporter;
import com.practice.spring.service.note.exporter.JsonExporter;
import com.practice.spring.service.note.exporter.NoteExporter;
import com.practice.spring.support.factory.NoteFactory;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonExporterTest {

    private final NoteExporter exporter = new JsonExporter(new ObjectMapper());

    @Test
    void export_ShouldReturnCorrectExportNotesResult(){
        List<Note> notes = List.of(NoteFactory.note(1L));

        ExportNotesResult result = exporter.export(notes);

        String json = new String(result.data(), StandardCharsets.UTF_8);

        assertTrue(json.contains(NoteFactory.title));
        assertTrue(json.contains(NoteFactory.body));

        assertEquals(MediaType.APPLICATION_JSON, result.contentType());
    }
}
