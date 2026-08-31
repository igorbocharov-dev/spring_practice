package com.practice.spring.event;

import com.practice.spring.entity.note.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Component
public final class NoteEventManager {

    private final Clock clock;

    @Autowired
    public NoteEventManager(Clock clock) {
        this.clock = clock;
    }

    public NoteEvent noteEvent(Note note, EventType type){
        return new NoteEvent(
                note.getId(),
                note.getAuthor(),
                UUID.randomUUID(),
                type,
                Instant.now(clock));
    }
}
