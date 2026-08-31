package com.practice.spring.service.noteRevision;

import com.practice.spring.dto.noteRevision.NoteRevisionResponse;
import com.practice.spring.dto.paging.SliceResponse;
import com.practice.spring.entity.note.Note;
import com.practice.spring.entity.noteRevision.NoteRevision;
import com.practice.spring.mapper.NoteRevisionMapper;
import com.practice.spring.repository.note.NoteRevisionRepository;
import com.practice.spring.util.validator.note.NoteValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class NoteRevisionServiceImpl implements NoteRevisionService {

    private final NoteRevisionRepository noteRevisionRepository;
    private final NoteValidator noteValidator;
    private final NoteRevisionMapper noteRevisionMapper;

    @Autowired
    public NoteRevisionServiceImpl(NoteRevisionRepository noteRevisionRepository, NoteValidator noteValidator, NoteRevisionMapper noteRevisionMapper) {
        this.noteRevisionRepository = noteRevisionRepository;
        this.noteValidator = noteValidator;
        this.noteRevisionMapper = noteRevisionMapper;
    }

    @Override
    @Transactional
    public void save(Note note){
        noteValidator.validate(note);
        noteRevisionRepository.save(new NoteRevision(note));
    }

    @PreAuthorize(value = "hasAuthority('notes.ADMIN')")
    @Override
    public SliceResponse<NoteRevisionResponse> getAllHistory(int page, int size){
        Slice<NoteRevision> noteRevisionPage = noteRevisionRepository.findAllBy
                (PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "changedAt")));
        List<NoteRevisionResponse> noteRevisionResponseList = noteRevisionPage.getContent()
                .stream().map(noteRevisionMapper::toResponse).toList();
        return new SliceResponse<>(noteRevisionResponseList, noteRevisionPage.hasNext());
    }
}
