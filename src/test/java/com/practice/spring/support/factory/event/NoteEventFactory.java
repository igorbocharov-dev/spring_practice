package com.practice.spring.support.factory.event;

import com.practice.spring.entity.note.Note;
import com.practice.spring.event.EventType;
import com.practice.spring.event.NoteEvent;

import java.time.Instant;
import java.util.UUID;

public class NoteEventFactory {

    public static NoteEvent noteEvent(Note note, EventType type){
        return new NoteEvent(note.getId(), note.getAuthor(), UUID.randomUUID(), type, Instant.now());
    }
}
