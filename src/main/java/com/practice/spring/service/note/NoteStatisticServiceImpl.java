package com.practice.spring.service.note;

import com.practice.spring.repository.note.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class NoteStatisticServiceImpl implements NoteStatisticService{

    private final NoteRepository noteRepository;

    @Autowired
    public NoteStatisticServiceImpl(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public long countNotes() {
        return noteRepository.count();
    }
}
