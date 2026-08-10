package com.practice.spring.unit.service.note;

import com.practice.spring.dto.note.CreateNoteRequest;
import com.practice.spring.dto.note.LocationNoteResponse;
import com.practice.spring.dto.note.NoteResponse;
import com.practice.spring.dto.note.UpdateNoteRequest;
import com.practice.spring.entity.note.Note;
import com.practice.spring.exception.note.NoteNotFoundException;
import com.practice.spring.mapper.NoteMapper;
import com.practice.spring.repository.note.NoteRepository;
import com.practice.spring.service.note.NoteService;
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
public class NoteServiceTest {

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

    @InjectMocks
    private NoteService noteService;

    @Test
    void createNote_ShouldCreateEntityAndReturnLocation(){
        CreateNoteRequest createNoteRequest = NoteFactory.createNoteRequest();
        Long id = 1L;
        LocationNoteResponse expectedResponse = NoteFactory.locationNoteResponse(id);

        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> {
            Note savedNote = invocation.getArgument(0);
            savedNote.setId(id);
            return savedNote;
        });

        LocationNoteResponse response = noteService.createNote(createNoteRequest);

        verify(noteLimitValidator).validate(any(Integer.class));

        assertEquals(expectedResponse.location(), response.location());

        ArgumentCaptor<Note> noteCapture = ArgumentCaptor.forClass(Note.class);
        verify(noteRepository).save(noteCapture.capture());

        Note savedNote = noteCapture.getValue();

        assertEquals(savedNote.getTitle(), createNoteRequest.title());
        assertEquals(savedNote.getBody(), createNoteRequest.body());

        verify(notesCreatedCounter).increment();
    }

    @Test
    void findById_ShouldReturnNoteResponse_WhenIdIsCorrectly(){
        Long id = 1L;
        Note note = NoteFactory.note(id);
        NoteResponse expectedResponse = NoteFactory.noteResponse();
        when(noteRepository.findById(id)).thenReturn(Optional.of(note));
        when(noteMapper.toNoteResponse(note)).thenReturn(expectedResponse);

        NoteResponse response = noteService.findById(id);

        verify(idValidator).validate(id);
        verify(noteRepository).findById(id);

        assertEquals(expectedResponse.title(), response.title());
        assertEquals(expectedResponse.body(), response.body());
    }

    @Test
    void findById_ShouldThrowNoteNotFoundException_WhenIdIsInvalid(){
        Long id = 999L;
        when(noteRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoteNotFoundException.class, ()-> noteService.findById(id));
    }

    @Test
    void update_ShouldReturnUpdatedNoteResponse(){
        Long id = 1L;
        Note previousValue = NoteFactory.note(id);
        UpdateNoteRequest updateNoteRequest = NoteFactory.updateNoteRequest();
        NoteResponse expectedNoteResponse = NoteFactory.updatedNoteResponse(updateNoteRequest);

        when(noteRepository.update(eq(id), any(Note.class))).thenReturn(previousValue);
        when(noteMapper.toNoteResponse(any(Note.class))).thenReturn(expectedNoteResponse);

        NoteResponse response = noteService.update(id, updateNoteRequest);

        verify(idValidator).validate(id);
        verify(noteRepository).update(eq(id), any(Note.class));
        verify(noteMapper).toNoteResponse(any(Note.class));

        assertEquals(expectedNoteResponse.title(), response.title());
        assertEquals(expectedNoteResponse.body(), response.body());

    }

    @Test
    void update_ShouldThrowNoteNotFoundException_WhenNoteDoesNotExists(){
        Long id = 1L;
        UpdateNoteRequest updateNoteRequest = NoteFactory.updateNoteRequest();

        when(noteRepository.update(eq(id), any(Note.class))).thenReturn(null);

        assertThrows(NoteNotFoundException.class, () -> noteService.update(id, updateNoteRequest));

        verify(idValidator).validate(id);
        verify(noteRepository).update(any(Long.class), any(Note.class));
        verifyNoInteractions(noteMapper);
    }
}
