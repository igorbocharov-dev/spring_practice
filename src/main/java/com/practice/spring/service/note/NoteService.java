package com.practice.spring.service.note;

import com.practice.spring.dto.note.*;
import com.practice.spring.entity.note.Note;
import com.practice.spring.exception.note.NoteNotFoundException;
import com.practice.spring.mapper.NoteMapper;
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
    private final NoteMapper noteMapper;

    @Autowired
    public NoteService(IdValidator idValidator,
                       NoteLimitValidator noteLimitValidator,
                       @Qualifier(value = "NoteRepository") NoteRepository noteRepository,
                       Counter notesCreatedCounter, NoteMapper noteMapper) {
        this.idValidator = idValidator;
        this.noteLimitValidator = noteLimitValidator;
        this.noteRepository = noteRepository;
        this.notesCreatedCounter = notesCreatedCounter;
        this.noteMapper = noteMapper;
    }

    public NotesResponse findAll(){
        List<NoteResponse> notes = noteRepository.findAll().stream().map(noteMapper::toNoteResponse).toList();
        return new NotesResponse(notes);
    }

    public LocationNoteResponse createNote(CreateNoteRequest createNoteRequest){
        noteLimitValidator.validate(noteRepository.count());
        Note savedNote = noteRepository.save(new Note(createNoteRequest.title(), createNoteRequest.body()));
        Long id = savedNote.getId();
        notesCreatedCounter.increment();
        log.info("Creating note with id: {}", id);
        return new LocationNoteResponse(URI.create("/notes/" + id));
    }

    public NoteResponse findById(Long id){
        idValidator.validate(id);
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException("Заметка с id " + id + " не найдена"));
        return noteMapper.toNoteResponse(note);
    }

    public NoteResponse update(Long id, UpdateNoteRequest updateNoteRequest){
        idValidator.validate(id);
        Note newValue = new Note(updateNoteRequest.title(), updateNoteRequest.body());
        Note previousValue = noteRepository.update(id, newValue);
        if(previousValue == null) {
            throw new NoteNotFoundException("Заметки с id " + id + " не существует");
        }
        return noteMapper.toNoteResponse(newValue);
    }

    public void delete(Long id){
        idValidator.validate(id);
        noteRepository.deleteById(id);
    }
}
