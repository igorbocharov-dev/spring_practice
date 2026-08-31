package com.practice.spring.unit.service.note;

import com.practice.spring.dto.note.CreateNoteRequest;
import com.practice.spring.dto.note.LocationNoteResponse;
import com.practice.spring.dto.note.NoteResponse;
import com.practice.spring.dto.note.UpdateNoteRequest;
import com.practice.spring.entity.note.Note;
import com.practice.spring.exception.note.NoteNotFoundException;
import com.practice.spring.mapper.NoteMapper;
import com.practice.spring.repository.note.NoteRepository;
import com.practice.spring.service.note.NoteServiceImpl;
import com.practice.spring.service.noteRevision.NoteRevisionService;
import com.practice.spring.support.factory.NoteFactory;
import com.practice.spring.util.validator.IdValidator;
import com.practice.spring.util.validator.note.NoteLimitValidator;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NoteServiceImplTest {

    @Mock
    private IdValidator idValidator;
    @Mock
    private NoteLimitValidator noteLimitValidator;
    @Mock
    private NoteRepository noteRepository;
    @Mock
    private Counter notesCreatedCounter;
    @Mock
    private NoteMapper noteMapper;
    @Mock
    private NoteRevisionService noteRevisionService;

    @InjectMocks
    private NoteServiceImpl noteServiceImpl;

    @Test
    void createNote_ShouldCreateEntityAndReturnLocation(){
        Long id = 1L;
        Note note = NoteFactory.note(id);
        CreateNoteRequest createNoteRequest = NoteFactory.createNoteRequest();
        LocationNoteResponse expectedResponse = NoteFactory.locationNoteResponse(id);

        when(noteMapper.toEntity(createNoteRequest)).thenReturn(note);
        when(noteRepository.save(note)).thenReturn(note);

        LocationNoteResponse response = noteServiceImpl.create(createNoteRequest);

        verify(noteLimitValidator).validate(any(Long.class));

        assertEquals(expectedResponse.location(), response.location());

        ArgumentCaptor<Note> noteCapture = ArgumentCaptor.forClass(Note.class);
        verify(noteRepository).save(noteCapture.capture());

        Note savedNote = noteCapture.getValue();

        assertEquals(savedNote.getTitle(), createNoteRequest.title());
        assertEquals(savedNote.getText(), createNoteRequest.text());

        verify(notesCreatedCounter).increment();
    }

    @Test
    void getNoteResponseById_ShouldReturnNoteResponse_WhenIdIsCorrectly(){
        Long id = 1L;
        Note note = NoteFactory.note(id);
        NoteResponse expectedResponse = NoteFactory.noteResponse();
        when(noteRepository.findById(id)).thenReturn(Optional.of(note));
        when(noteMapper.toNoteResponse(note)).thenReturn(expectedResponse);

        NoteResponse response = noteServiceImpl.getNoteResponseById(id);

        verify(idValidator).validate(id);
        verify(noteRepository).findById(id);

        assertEquals(expectedResponse.title(), response.title());
        assertEquals(expectedResponse.text(), response.text());
    }

    @Test
    void getNoteResponseById_ShouldThrowNoteNotFoundException_WhenIdIsInvalid(){
        Long id = 999L;
        when(noteRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoteNotFoundException.class, ()-> noteServiceImpl.getNoteResponseById(id));
    }

    @Test
    void update_ShouldReturnUpdatedNoteResponse(){
        Long id = 1L;
        Note previousValue = NoteFactory.note(id);
        UpdateNoteRequest updateNoteRequest = NoteFactory.updateNoteRequest();
        NoteResponse expectedNoteResponse = NoteFactory.updatedNoteResponse(updateNoteRequest, previousValue.getAuthor());

        when(noteRepository.findById(id)).thenReturn(Optional.of(previousValue));
        when(noteMapper.toNoteResponse(previousValue)).thenReturn(expectedNoteResponse);

        NoteResponse response = noteServiceImpl.update(id, updateNoteRequest);

        verify(idValidator).validate(id);
        verify(noteRepository).findById(id);
        verify(noteMapper).toNoteResponse(previousValue);

        assertEquals(expectedNoteResponse.title(), response.title());
        assertEquals(expectedNoteResponse.text(), response.text());

    }

    @Test
    void update_ShouldThrowNoteNotFoundException_WhenNoteDoesNotExists(){
        Long id = 999L;
        UpdateNoteRequest updateNoteRequest = NoteFactory.updateNoteRequest();

        when(noteRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoteNotFoundException.class, () -> noteServiceImpl.update(id, updateNoteRequest));

        verify(idValidator).validate(id);
        verify(noteRepository).findById(id);
        verifyNoInteractions(noteMapper);
    }
}
