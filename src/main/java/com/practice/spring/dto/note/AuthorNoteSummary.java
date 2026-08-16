package com.practice.spring.dto.note;

import java.time.Instant;

public record AuthorNoteSummary(Long countOfPersonalNotes, Instant dateOfLastNote) {}