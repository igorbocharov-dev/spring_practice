package com.practice.spring.service.note.exporter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.practice.spring.dto.note.ExportNotesResult;
import com.practice.spring.entity.note.Note;
import com.practice.spring.exception.note.ParseNoteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(value = "XML")
public class XmlExporter implements NoteExporter{

    private final XmlMapper xmlMapper;

    @Autowired
    public XmlExporter(XmlMapper xmlMapper) {
        this.xmlMapper = xmlMapper;
    }

    @Override
    public ExportNotesResult export(List<Note> notes) {
        final byte [] data;
        try {
            data = xmlMapper.writeValueAsBytes(notes);
        } catch (JsonProcessingException e) {
            throw new ParseNoteException("Ошибка при записи значения 'notes' в XML", e.getCause());
        }
        return new ExportNotesResult(data, MediaType.APPLICATION_XML);
    }
}
