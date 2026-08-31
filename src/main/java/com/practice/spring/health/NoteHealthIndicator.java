package com.practice.spring.health;

import com.practice.spring.config.note.NoteConfiguration;
import com.practice.spring.service.note.NoteStatisticService;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class NoteHealthIndicator implements HealthIndicator {

    private final NoteStatisticService noteStatisticService;
    private final NoteConfiguration noteConfiguration;

    @Autowired
    public NoteHealthIndicator(NoteStatisticService noteStatisticService, NoteConfiguration noteConfiguration) {
        this.noteStatisticService = noteStatisticService;
        this.noteConfiguration = noteConfiguration;
    }

    @Override
    public @Nullable Health health() {
        long countNotes = noteStatisticService.countNotes();
        long limitNotes = noteConfiguration.getLimit();

        if(countNotes >= limitNotes){
            return Health.down()
                    .withDetail("Cause", "Note limit reached")
                    .withDetail("countNotes", countNotes)
                    .withDetail("limitNotes", limitNotes)
                    .build();
        }

        return Health.up()
                .withDetail("countNotes", countNotes)
                .withDetail("limitNotes", limitNotes)
                .build();
    }
}
