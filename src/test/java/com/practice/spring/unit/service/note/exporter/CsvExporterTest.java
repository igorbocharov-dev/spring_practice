package com.practice.spring.unit.service.note.exporter;

import com.practice.spring.dto.note.ExportNotesResult;
import com.practice.spring.entity.note.Note;
import com.practice.spring.service.note.exporter.CsvExporter;
import com.practice.spring.service.note.exporter.NoteExporter;
import com.practice.spring.support.factory.NoteFactory;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CsvExporterTest {

    private final NoteExporter exporter = new CsvExporter();

    @Test
    void export_ShouldReturnCorrectExportNotesResult(){
        List<Note> notes = List.of(NoteFactory.note(1L));

        ExportNotesResult result = exporter.export(notes);

        String csv = new String(result.data(), StandardCharsets.UTF_8);

        assertTrue(csv.contains(NoteFactory.title));
        assertTrue(csv.contains(NoteFactory.text));

        assertEquals(MediaType.parseMediaType("text/csv"), result.contentType());
    }
}
