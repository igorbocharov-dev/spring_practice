package com.practice.spring.health;

import com.practice.spring.config.note.NoteConfiguration;
import com.practice.spring.repository.note.NoteRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class NoteHealthIndicator implements HealthIndicator {

    private final NoteRepository noteRepository;
    private final NoteConfiguration noteConfiguration;

    @Autowired
    public NoteHealthIndicator(@Qualifier(value = "NoteRepository") NoteRepository noteRepository,
                               NoteConfiguration noteConfiguration) {
        this.noteRepository = noteRepository;
        this.noteConfiguration = noteConfiguration;
    }

    @Override
    public @Nullable Health health() {
        long currentNotes = noteRepository.count();
        long limitNotes = noteConfiguration.getLimit();

        if(currentNotes >= limitNotes){
            return Health.down()
                    .withDetail("Cause", "Note limit reached")
                    .withDetail("currentNotes", currentNotes)
                    .withDetail("limitNotes", limitNotes)
                    .build();
        }

        return Health.up()
                .withDetail("currentNotes", currentNotes)
                .withDetail("limitNotes", limitNotes)
                .build();
    }
}
