package com.practice.spring.event;

import java.time.Instant;
import java.util.UUID;

public record NoteEvent(Long noteId, String author, UUID eventId, EventType type, Instant occurredAt) {}