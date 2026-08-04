package com.practice.spring.service.note.exporter;

import com.practice.spring.entity.note.Note;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(value = "CSV")
public class CsvExporter implements NoteExporter{
    @Override
    public String export(List<Note> notes) {
        return "CSV export: \n" +   Arrays.toString(notes.toArray());
    }
}
