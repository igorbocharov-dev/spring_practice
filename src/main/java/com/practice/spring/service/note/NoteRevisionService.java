package com.practice.spring.service.note;

import com.practice.spring.dto.noteRevision.NoteRevisionResponse;
import com.practice.spring.dto.paging.SliceResponse;
import com.practice.spring.entity.note.Note;
import com.practice.spring.entity.note.NoteRevision;
import com.practice.spring.mapper.NoteRevisionMapper;
import com.practice.spring.repository.note.NoteRevisionRepository;
import com.practice.spring.util.validator.note.NoteValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class NoteRevisionService {

    private final NoteRevisionRepository noteRevisionRepository;
    private final NoteValidator noteValidator;
    private final NoteRevisionMapper noteRevisionMapper;

    @Autowired
    public NoteRevisionService(NoteRevisionRepository noteRevisionRepository, NoteValidator noteValidator, NoteRevisionMapper noteRevisionMapper) {
        this.noteRevisionRepository = noteRevisionRepository;
        this.noteValidator = noteValidator;
        this.noteRevisionMapper = noteRevisionMapper;
    }

    @Transactional
    public void savePreviousState(Note note){
        noteValidator.validate(note);
        noteRevisionRepository.save(new NoteRevision(note));
    }

    public SliceResponse<NoteRevisionResponse> getAllHistory(int page, int size){
        Slice<NoteRevision> noteRevisionPage = noteRevisionRepository.findAllBy(PageRequest.of(page, size));
        List<NoteRevisionResponse> noteRevisionResponseList = noteRevisionPage.getContent()
                .stream().map(noteRevisionMapper::toResponse).toList();
        return new SliceResponse<>(noteRevisionResponseList, noteRevisionPage.hasNext());
    }
}
