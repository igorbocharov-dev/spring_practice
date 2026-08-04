package com.practice.spring.service.note.exporter;

import com.practice.spring.entity.note.Note;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component(value = "JSON")
public class JsonExporter implements NoteExporter{
    @Override
    public String export(List<Note> notes) {
        return "JSON export: \n" +   Arrays.toString(notes.toArray());
    }
}
