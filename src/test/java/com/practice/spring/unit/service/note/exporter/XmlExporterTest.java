package com.practice.spring.unit.service.note.exporter;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.practice.spring.dto.note.ExportNotesResult;
import com.practice.spring.entity.note.Note;
import com.practice.spring.service.note.exporter.NoteExporter;
import com.practice.spring.service.note.exporter.XmlExporter;
import com.practice.spring.support.factory.note.NoteFactory;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XmlExporterTest {

    private final NoteExporter exporter = new XmlExporter(new XmlMapper());

    @Test
    void export_ShouldReturnCorrectExportNotesResult(){
        List<Note> notes = List.of(NoteFactory.note(1L));

        ExportNotesResult result = exporter.export(notes);

        String json = new String(result.data(), StandardCharsets.UTF_8);

        assertTrue(json.contains(NoteFactory.title));
        assertTrue(json.contains(NoteFactory.text));

        assertEquals(MediaType.APPLICATION_XML, result.contentType());
    }
}
