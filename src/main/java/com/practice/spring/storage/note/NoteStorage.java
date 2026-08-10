package com.practice.spring.storage.note;

import com.practice.spring.entity.note.Note;
import com.practice.spring.storage.Storage;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class NoteStorage implements Storage<Long, Note> {

    private final ConcurrentMap<Long, Note> db = new ConcurrentHashMap<>();

    @Override
    public Collection<Note> values() {
        return db.values();
    }

    @Override
    public Note put(Long key, Note value) {
        return db.put(key, value);
    }

    @Override
    public Note get(Long key) {
        return db.get(key);
    }

    @Override
    public Note replace(Long key, Note value) {
        return db.replace(key, value);
    }

    @Override
    public Note remove(Long key) {
        return db.remove(key);
    }

    @Override
    public int size() {
        return db.size();
    }
}
