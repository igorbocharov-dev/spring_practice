package com.practice.spring.service.note;

import com.practice.spring.dto.note.CreateNoteRequest;
import com.practice.spring.dto.note.LocationNoteResponse;
import com.practice.spring.dto.note.NoteResponse;
import com.practice.spring.dto.note.UpdateNoteRequest;
import com.practice.spring.entity.note.Note;
import com.practice.spring.exception.note.NoteNotFoundException;
import com.practice.spring.repository.note.NoteRepository;
import com.practice.spring.util.validator.IdValidator;
import com.practice.spring.util.validator.note.NoteLimitValidator;
import io.micrometer.core.instrument.Counter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Slf4j
@Service
public class NoteService {

    private final IdValidator idValidator;
    private final NoteLimitValidator noteLimitValidator;
    private final NoteRepository noteRepository;
    private final Counter notesCreatedCounter;

    @Autowired
    public NoteService(IdValidator idValidator,
                       NoteLimitValidator noteLimitValidator,
                       @Qualifier(value = "NoteRepository") NoteRepository noteRepository, Counter notesCreatedCounter) {
        this.idValidator = idValidator;
        this.noteLimitValidator = noteLimitValidator;
        this.noteRepository = noteRepository;
        this.notesCreatedCounter = notesCreatedCounter;
    }

    public List<Note> findAll(){
        return noteRepository.findAll();
    }

    public LocationNoteResponse createNote(CreateNoteRequest createNoteRequest){
        noteLimitValidator.validate(noteRepository.count());
        Long id = noteRepository.save(new Note(createNoteRequest.title(), createNoteRequest.body()));
        notesCreatedCounter.increment();
        log.info("Creating note with id: {}", id);
        return new LocationNoteResponse(URI.create("/note/" + id));
    }

    public NoteResponse findById(Long id){
        idValidator.validate(id);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException("Заметка с id " + id + " не найдена"));
        return new NoteResponse(note.getTitle(), note.getBody());
    }

    public NoteResponse update(Long id, UpdateNoteRequest updateNoteRequest){
        idValidator.validate(id);
        Note note = noteRepository.update(id, new Note(updateNoteRequest.title(), updateNoteRequest.body()));
        return new NoteResponse(note.getTitle(), note.getBody());
    }

    public void delete(Long id){
        idValidator.validate(id);
        noteRepository.deleteById(id);
    }
}
