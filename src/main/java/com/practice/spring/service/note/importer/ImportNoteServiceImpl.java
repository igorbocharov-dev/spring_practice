package com.practice.spring.service.note.importer;

import com.practice.spring.dto.note.CreateNoteRequest;
import com.practice.spring.dto.note.ImportNotesRequest;
import com.practice.spring.mapper.note.NoteMapper;
import com.practice.spring.repository.note.NoteRepository;
import com.practice.spring.util.validator.note.ImportNotesLimitValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional(readOnly = true)
public class ImportNoteServiceImpl implements ImportNoteService{

    private final ImportNotesLimitValidator importNotesLimitValidator;
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    @Autowired
    public ImportNoteServiceImpl(ImportNotesLimitValidator importNotesLimitValidator,
                                 NoteRepository noteRepository,
                                 NoteMapper noteMapper) {
        this.importNotesLimitValidator = importNotesLimitValidator;
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
    }

    @Override
    @Transactional
    @CacheEvict(value = "author_summary", key = "#author")
    public void importNotes(String author, ImportNotesRequest request){
        AtomicInteger count = new AtomicInteger();
        List<CreateNoteRequest> createNotesRequest = request.notes();
        for (CreateNoteRequest createNoteRequest : createNotesRequest) {
            noteRepository.save(noteMapper.toEntity(author, createNoteRequest));
            importNotesLimitValidator.validate(count.incrementAndGet());
        }
    }
}
