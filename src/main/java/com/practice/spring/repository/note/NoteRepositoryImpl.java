package com.practice.spring.repository.note;

import com.practice.spring.entity.note.Note;
import com.practice.spring.storage.note.NoteStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component(value = "NoteRepository")
public class NoteRepositoryImpl implements NoteRepository{

    private final AtomicLong incrementId = new AtomicLong();

    private final NoteStorage storage;

    @Autowired
    public NoteRepositoryImpl(NoteStorage storage) {
        this.storage = storage;
    }

    @Override
    public List<Note> findAll() {
        return storage.values().stream().toList();
    }

    @Override
    public Long save(Note note) {
        Long id = incrementId.incrementAndGet();
        note.setId(id);
        storage.put(id, note);
        log.info("Save note with id: {}", id);
        return id;
    }

    @Override
    public Optional<Note> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Note update(Long id, Note newNote) {
        return storage.replace(id, newNote);
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public int count() {
        return storage.size();
    }
}
