package com.practice.spring.repository.note;

import com.practice.spring.entity.note.Note;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository {

    List<Note> findAll();

    Note save(Note note);

    Optional<Note> findById(Long id);

    Note update(Long id, Note newNote);

    void deleteById(Long id);

    int count();
}
