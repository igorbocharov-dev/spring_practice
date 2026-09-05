package com.practice.spring.unit.service.note;

import com.practice.spring.dto.note.CreateNoteRequest;
import com.practice.spring.dto.note.LocationNoteResponse;
import com.practice.spring.dto.note.NoteResponse;
import com.practice.spring.dto.note.UpdateNoteRequest;
import com.practice.spring.entity.note.Note;
import com.practice.spring.event.NoteEventType;
import com.practice.spring.event.NoteEvent;
import com.practice.spring.event.NoteEventManager;
import com.practice.spring.event.NoteEventProducer;
import com.practice.spring.exception.note.NoteNotFoundException;
import com.practice.spring.mapper.note.NoteMapper;
import com.practice.spring.repository.note.NoteRepository;
import com.practice.spring.service.note.NoteServiceImpl;
import com.practice.spring.service.noteRevision.NoteRevisionService;
import com.practice.spring.support.factory.event.NoteEventFactory;
import com.practice.spring.support.factory.note.NoteFactory;
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

import static org.junit.jupiter.api.Assertions.*;
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
    @Mock
    private NoteEventProducer noteEventProducer;
    @Mock
    private NoteEventManager noteEventManager;

    @InjectMocks
    private NoteServiceImpl noteServiceImpl;

    @Test
    void createNote_ShouldCreateEntityAndReturnLocation(){
        Note note = NoteFactory.note(1L);
        CreateNoteRequest createNoteRequest = NoteFactory.createNoteRequest();
        LocationNoteResponse expectedResponse = NoteFactory.locationNoteResponse(note.getId());

        NoteEvent event = NoteEventFactory.noteEvent(note, NoteEventType.CREATED);

        when(noteMapper.toEntity(note.getAuthor(), createNoteRequest)).thenReturn(note);
        when(noteRepository.save(note)).thenReturn(note);
        when(noteEventManager.noteEvent(note, NoteEventType.CREATED)).thenReturn(event);

        LocationNoteResponse response = noteServiceImpl.create(note.getAuthor(), createNoteRequest);

        verify(noteLimitValidator).validate(any(Long.class));

        assertEquals(expectedResponse.location(), response.location());

        ArgumentCaptor<Note> noteCapture = ArgumentCaptor.forClass(Note.class);
        verify(noteRepository).save(noteCapture.capture());

        Note savedNote = noteCapture.getValue();

        assertEquals(savedNote.getTitle(), createNoteRequest.title());
        assertEquals(savedNote.getText(), createNoteRequest.text());

        verify(notesCreatedCounter).increment();

        ArgumentCaptor<NoteEvent> eventArgumentCaptor = ArgumentCaptor.forClass(NoteEvent.class);
        verify(noteEventProducer).send(eventArgumentCaptor.capture());

        NoteEvent actualEvent = eventArgumentCaptor.getValue();
        assertSame(event, actualEvent);
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

        NoteEvent event = NoteEventFactory.noteEvent(previousValue, NoteEventType.UPDATED);

        when(noteRepository.findById(id)).thenReturn(Optional.of(previousValue));
        when(noteMapper.toNoteResponse(previousValue)).thenReturn(expectedNoteResponse);
        when(noteEventManager.noteEvent(previousValue, NoteEventType.UPDATED)).thenReturn(event);

        NoteResponse response = noteServiceImpl.update(id, previousValue.getAuthor(), updateNoteRequest);

        verify(idValidator).validate(id);
        verify(noteRepository).findById(id);
        verify(noteMapper).toNoteResponse(previousValue);

        assertEquals(expectedNoteResponse.title(), response.title());
        assertEquals(expectedNoteResponse.text(), response.text());

        ArgumentCaptor<NoteEvent> eventArgumentCaptor = ArgumentCaptor.forClass(NoteEvent.class);
        verify(noteEventProducer).send(eventArgumentCaptor.capture());

        NoteEvent actualEvent = eventArgumentCaptor.getValue();
        assertSame(event, actualEvent);
    }

    @Test
    void update_ShouldThrowNoteNotFoundException_WhenNoteDoesNotExists(){
        Long id = 999L;
        UpdateNoteRequest updateNoteRequest = NoteFactory.updateNoteRequest();

        when(noteRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoteNotFoundException.class, () -> noteServiceImpl.update(id, "Pavel Durov", updateNoteRequest));

        verify(idValidator).validate(id);
        verify(noteRepository).findById(id);
        verifyNoInteractions(noteEventManager, noteEventProducer, noteMapper);
    }
}
