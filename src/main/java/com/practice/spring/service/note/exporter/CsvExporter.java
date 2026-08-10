package com.practice.spring.service.note.exporter;

import com.practice.spring.dto.note.ExportNotesResult;
import com.practice.spring.entity.note.Note;
import com.practice.spring.exception.note.ParseNoteException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component(value = "CSV")
public class CsvExporter implements NoteExporter{

    @Override
    public ExportNotesResult export(List<Note> notes) {
        StringWriter writer = new StringWriter();

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("id", "title", "body")
                .build();

        try (CSVPrinter printer = new CSVPrinter(writer, format)) {
            for (Note note : notes) {
                printer.printRecord(
                        note.getId(),
                        note.getTitle(),
                        note.getBody()
                );
            }
        } catch (IOException e) {
            throw new ParseNoteException("Ошибка при записи значения 'notes' в CSV", e);
        }
        byte [] data = writer.toString()
                .getBytes(StandardCharsets.UTF_8);

        return new ExportNotesResult(data, MediaType.parseMediaType("text/csv"));
    }

}
