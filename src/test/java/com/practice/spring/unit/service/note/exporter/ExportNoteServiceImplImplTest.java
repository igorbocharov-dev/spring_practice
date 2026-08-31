package com.practice.spring.unit.service.note.exporter;

import com.practice.spring.dto.note.ExportNotesResult;
import com.practice.spring.entity.note.Note;
import com.practice.spring.exception.note.IllegalNoteExportFormatException;
import com.practice.spring.exception.note.NoteNotFoundException;
import com.practice.spring.exception.note.UnsupportedNoteExportFormatException;
import com.practice.spring.repository.note.NoteRepository;
import com.practice.spring.service.note.exporter.*;
import com.practice.spring.support.factory.note.NoteFactory;
import com.practice.spring.util.validator.note.NotesValidator;
import com.practice.spring.util.validator.note.exporter.ExportNoteFormatValidator;
import com.practice.spring.util.validator.note.exporter.NoteExporterValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExportNoteServiceImplImplTest {

    @Mock
    private JsonExporter jsonExporter;
    @Mock
    private CsvExporter csvExporter;
    @Mock
    private XmlExporter xmlExporter;

    private Map<String, NoteExporter> exporters;

    private final NoteExporterValidator noteExporterValidator = new NoteExporterValidator();
    private final ExportNoteFormatValidator exportNoteFormatValidator = new ExportNoteFormatValidator();
    private final NotesValidator notesValidator = new NotesValidator();

    @Mock
    private NoteRepository noteRepository;

    private ExportNoteService exportNoteService;

    private static final String jsonFormat = "JSON";
    private static final String csvFormat = "CSV";
    private static final String xmlFormat = "XML";

    @BeforeEach
    void setUp(){
        exporters = Map.of(jsonFormat, jsonExporter, csvFormat, csvExporter, xmlFormat, xmlExporter);

        exportNoteService = new ExportNoteServiceImpl(
                exporters, noteExporterValidator, exportNoteFormatValidator, notesValidator, noteRepository
        );
    }

    @Test
    void export_ShouldDelegateToJsonExporter() {
        List<Note> notes = List.of(NoteFactory.note(1L));
        ExportNotesResult expected = new ExportNotesResult
                ("json".getBytes(), MediaType.APPLICATION_JSON);

        when(noteRepository.findAll()).thenReturn(notes);
        when(jsonExporter.export(notes)).thenReturn(expected);

        ExportNotesResult actual = exportNoteService.export(jsonFormat);
        assertSame(expected, actual);

        verify(noteRepository).findAll();
        verify(csvExporter, never()).export(anyList());
        verify(xmlExporter, never()).export(anyList());
    }

    @Test
    void export_ShouldDelegateToCsvExporter(){
        List<Note> notes = List.of(NoteFactory.note(1L));
        ExportNotesResult expected = new ExportNotesResult
                ("csv".getBytes(), MediaType.parseMediaType("text/csv"));

        when(noteRepository.findAll()).thenReturn(notes);
        when(csvExporter.export(notes)).thenReturn(expected);

        ExportNotesResult actual = exportNoteService.export(csvFormat);
        assertSame(expected, actual);

        verify(noteRepository).findAll();
        verify(jsonExporter, never()).export(anyList());
        verify(xmlExporter, never()).export(anyList());
    }

    @Test
    void export_ShouldDelegateToXmlExporter(){
        List<Note> notes = List.of(NoteFactory.note(1L));
        ExportNotesResult expected = new ExportNotesResult
                ("xml".getBytes(), MediaType.APPLICATION_XML);

        when(noteRepository.findAll()).thenReturn(notes);
        when(xmlExporter.export(notes)).thenReturn(expected);

        ExportNotesResult actual = exportNoteService.export(xmlFormat);
        assertSame(expected, actual);

        verify(noteRepository).findAll();
        verify(jsonExporter, never()).export(anyList());
        verify(csvExporter, never()).export(anyList());
    }


    @Test
    void export_ShouldThrowIllegalNoteExportFormatException_WhenFormatIsNull(){
        assertThrows(IllegalNoteExportFormatException.class, () -> exportNoteService.export(null));
    }

    @Test
    void export_ShouldThrowIllegalNoteExportFormatException_WhenFormatIsBlank(){
        assertThrows(IllegalNoteExportFormatException.class, () -> exportNoteService.export(" "));
    }

    @Test
    void export_ShouldThrowUnsupportedNoteExportFormatException_WhenExporterIsNotExists(){
        assertThrows(UnsupportedNoteExportFormatException.class, () -> exportNoteService.export("incorrect format"));
    }

    @Test
    void export_ShouldThrowNoteNotFoundException_WhenNotesIsNull(){
        when(noteRepository.findAll()).thenReturn(null);
        assertThrows(NoteNotFoundException.class, () -> exportNoteService.export(jsonFormat));
    }

    @Test
    void export_ShouldThrowNoteNotFoundException_WhenNotesIsEmpty(){
        when(noteRepository.findAll()).thenReturn(List.of());
        assertThrows(NoteNotFoundException.class, () -> exportNoteService.export(jsonFormat));
    }
}
