package com.practice.spring.health;

import com.practice.spring.config.note.NoteConfiguration;
import com.practice.spring.service.note.NoteService;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class NoteHealthIndicator implements HealthIndicator {

    private final NoteService noteService;
    private final NoteConfiguration noteConfiguration;

    @Autowired
    public NoteHealthIndicator(NoteService noteService, NoteConfiguration noteConfiguration) {
        this.noteService = noteService;
        this.noteConfiguration = noteConfiguration;
    }

    @Override
    public @Nullable Health health() {
        long currentNotes = noteService.count();
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
