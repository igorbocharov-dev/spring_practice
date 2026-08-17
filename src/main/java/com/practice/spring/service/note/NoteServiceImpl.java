package com.practice.spring.service.note;

import com.practice.spring.dto.note.*;
import com.practice.spring.entity.note.Note;
import com.practice.spring.exception.note.AuthorNotFoundException;
import com.practice.spring.exception.note.NoteNotFoundException;
import com.practice.spring.mapper.NoteMapper;
import com.practice.spring.repository.note.NoteRepository;
import com.practice.spring.service.noteRevision.NoteRevisionService;
import com.practice.spring.util.validator.IdValidator;
import com.practice.spring.util.validator.note.NoteLimitValidator;
import io.micrometer.core.instrument.Counter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.Objects;

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
    private final CacheManager cacheManager;

    @Autowired
    public NoteServiceImpl(IdValidator idValidator,
                           NoteLimitValidator noteLimitValidator,
                           NoteRepository noteRepository,
                           Counter notesCreatedCounter, NoteMapper noteMapper, NoteRevisionService noteRevisionService, CacheManager cacheManager) {
        this.idValidator = idValidator;
        this.noteLimitValidator = noteLimitValidator;
        this.noteRepository = noteRepository;
        this.notesCreatedCounter = notesCreatedCounter;
        this.noteMapper = noteMapper;
        this.noteRevisionService = noteRevisionService;
        this.cacheManager = cacheManager;
    }

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
    @CacheEvict(value = "author_summary", key = "#createNoteRequest.author()")
    @Transactional
    public LocationNoteResponse create(CreateNoteRequest createNoteRequest){
        noteLimitValidator.validate(noteRepository.count());
        Note entityToCreate = noteMapper.toEntity(createNoteRequest);
        Note savedNote = noteRepository.save(entityToCreate);
        Long id = savedNote.getId();
        notesCreatedCounter.increment();
        log.info("Creating note with id: {}", id);
        return new LocationNoteResponse(URI.create("/notes/" + id));
    }

    @Override
    public NoteResponse getNoteResponseById(Long id){
        Note note = findById(id);
        return noteMapper.toNoteResponse(note);
    }

    @Override
    @Transactional
    @CacheEvict(value = "author_summary", key = "#result.author()")
    public NoteResponse update(Long id, UpdateNoteRequest updateNoteRequest){
        Note note = findById(id);
        noteRevisionService.save(note);
        note.setTitle(updateNoteRequest.title());
        note.setText(updateNoteRequest.text());
        return noteMapper.toNoteResponse(note);
    }

    @Override
    @Transactional
    public void delete(Long id){
        idValidator.validate(id);
        Note note = findById(id);
        String author = note.getAuthor();
        noteRepository.delete(note);
        Objects.requireNonNull(cacheManager.getCache("author_summary")).evict(author);
    }

    private Note findById(Long id){
        idValidator.validate(id);
        return noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException("Заметка с id " + id + " не найдена"));
    }

}
