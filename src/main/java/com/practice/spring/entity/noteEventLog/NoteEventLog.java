package com.practice.spring.entity.noteEventLog;

import com.practice.spring.event.NoteEventType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "note_event_log")
@Getter
@Setter
public class NoteEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "note_id")
    private Long noteId;

    @Column(name = "author")
    private String author;

    @Column(name = "event_id")
    private UUID eventId;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type")
    private NoteEventType type;

    @Column(name = "occurred_at", updatable = false)
    private Instant occurredAt;

    @Column(name = "received_at")
    private Instant receivedAt;

    public NoteEventLog() {}

    public NoteEventLog(Long noteId, String author, UUID eventId, NoteEventType type, Instant occurredAt, Instant receivedAt) {
        this.noteId = noteId;
        this.author = author;
        this.eventId = eventId;
        this.type = type;
        this.occurredAt = occurredAt;
        this.receivedAt = receivedAt;
    }
}
