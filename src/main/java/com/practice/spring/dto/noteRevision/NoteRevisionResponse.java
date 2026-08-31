package com.practice.spring.dto.noteRevision;

import java.time.Instant;

public record NoteRevisionResponse(Long noteId, String oldTitle, String oldText, Instant changedAt) {}