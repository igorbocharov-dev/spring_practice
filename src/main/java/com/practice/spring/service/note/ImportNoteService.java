package com.practice.spring.service.note;

import com.practice.spring.dto.note.CreateNoteRequest;
import com.practice.spring.dto.note.ImportNotesRequest;
import com.practice.spring.entity.note.Note;
import com.practice.spring.mapper.NoteMapper;
import com.practice.spring.repository.note.NoteRepository;
import com.practice.spring.util.validator.note.ImportNotesLimitValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional(readOnly = true)
public class ImportNoteService {

    private final ImportNotesLimitValidator importNotesLimitValidator;
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final CacheManager cacheManager;

    @Autowired
    public ImportNoteService(ImportNotesLimitValidator importNotesLimitValidator, NoteRepository noteRepository, NoteMapper noteMapper, CacheManager cacheManager) {
        this.importNotesLimitValidator = importNotesLimitValidator;
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
        this.cacheManager = cacheManager;
    }

    @Transactional
    public void importNotes(ImportNotesRequest request){
        AtomicInteger count = new AtomicInteger();
        List<CreateNoteRequest> createNotesRequest = request.notes();
        Set<String> authors = new HashSet<>();
        for (CreateNoteRequest createNoteRequest : createNotesRequest) {
            Note savedNote = noteRepository.save(noteMapper.toEntity(createNoteRequest));
            importNotesLimitValidator.validate(count.incrementAndGet());
            authors.add(savedNote.getAuthor());
        }
        authors.forEach(author -> Objects.requireNonNull(cacheManager.getCache("author_summary")).evict(author));
    }
}
