package com.practice.spring.service.note;

import com.practice.spring.dto.note.*;
import com.practice.spring.entity.note.Note;
import com.practice.spring.event.NoteEventType;
import com.practice.spring.event.NoteEvent;
import com.practice.spring.event.NoteEventManager;
import com.practice.spring.event.NoteEventProducer;
import com.practice.spring.exception.note.AuthorNotFoundException;
import com.practice.spring.exception.note.NoteNotFoundException;
import com.practice.spring.mapper.note.NoteMapper;
import com.practice.spring.repository.note.NoteRepository;
import com.practice.spring.service.noteRevision.NoteRevisionService;
import com.practice.spring.util.validator.IdValidator;
import com.practice.spring.util.validator.note.NoteLimitValidator;
import io.micrometer.core.instrument.Counter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class NoteServiceImpl implements NoteService{

    private final IdValidator idValidator;
    private final NoteLimitValidator noteLimitValidator;
    private final NoteRepository noteRepository;
    private final Counter notesCreatedCounter;
    private final NoteMapper noteMapper;
    private final NoteRevisionService noteRevisionService;
    private final NoteEventProducer noteEventProducer;
    private final NoteEventManager noteEventManager;

    @Autowired
    public NoteServiceImpl(IdValidator idValidator,
                           NoteLimitValidator noteLimitValidator,
                           NoteRepository noteRepository,
                           Counter notesCreatedCounter,
                           NoteMapper noteMapper,
                           NoteRevisionService noteRevisionService,
                           NoteEventProducer noteEventProducer,
                           NoteEventManager noteEventManager) {
        this.idValidator = idValidator;
        this.noteLimitValidator = noteLimitValidator;
        this.noteRepository = noteRepository;
        this.notesCreatedCounter = notesCreatedCounter;
        this.noteMapper = noteMapper;
        this.noteRevisionService = noteRevisionService;
        this.noteEventProducer = noteEventProducer;
        this.noteEventManager = noteEventManager;
    }

    @PreAuthorize(value = "hasAuthority('notes.ADMIN') or #author==authentication.name")
    @Override
    @Cacheable(value = "author_summary", key = "#author")
    public AuthorNoteSummary authorNoteSummary(String author){
        return noteRepository.getAuthorSummary(author).orElseThrow
                (() -> new AuthorNotFoundException(String.format("Автора: %s не существует: ", author)));
    }

    @Override
    public NotesResponse findAll(){
        List<NoteResponse> notes = noteRepository.findAll().stream().map(noteMapper::toNoteResponse).toList();
        return new NotesResponse(notes);
    }

    @Override
    @CacheEvict(value = "author_summary", key = "#author")
    @Transactional
    public LocationNoteResponse create(String author, CreateNoteRequest createNoteRequest){
        noteLimitValidator.validate(noteRepository.count());
        Note entityToCreate = noteMapper.toEntity(author, createNoteRequest);
        Note savedNote = noteRepository.save(entityToCreate);
        Long id = savedNote.getId();
        notesCreatedCounter.increment();
        log.info("Creating note with id: {}", id);
        NoteEvent event = noteEventManager.noteEvent(savedNote, NoteEventType.CREATED);
        noteEventProducer.send(event);
        return new LocationNoteResponse(URI.create("/notes/" + id));
    }

    @Override
    public NoteResponse getNoteResponseById(Long id){
        Note note = findById(id);
        return noteMapper.toNoteResponse(note);
    }

    @Override
    @Transactional
    @CacheEvict(value = "author_summary", key = "#author")
    public NoteResponse update(Long id, String author, UpdateNoteRequest updateNoteRequest){
        Note note = findById(id);
        if (!note.getAuthor().equals(author)){
            throw new AccessDeniedException("У вас не достаточно прав");
        }
        noteRevisionService.save(note);
        note.setTitle(updateNoteRequest.title());
        note.setText(updateNoteRequest.text());
        NoteEvent event = noteEventManager.noteEvent(note, NoteEventType.UPDATED);
        noteEventProducer.send(event);
        return noteMapper.toNoteResponse(note);
    }

    @Override
    @Transactional
    @CacheEvict(value = "author_summary", key = "#author")
    public void delete(Long id, String author){
        idValidator.validate(id);
        Note note = findById(id);
        if (!note.getAuthor().equals(author)){
            throw new AccessDeniedException("У вас не достаточно прав");
        }
        noteRepository.delete(note);
        NoteEvent event = noteEventManager.noteEvent(note, NoteEventType.DELETED);
        noteEventProducer.send(event);
    }

    private Note findById(Long id){
        idValidator.validate(id);
        return noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException("Заметка с id " + id + " не найдена"));
    }

}
