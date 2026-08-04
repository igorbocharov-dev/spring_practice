package com.practice.spring.util.validator.note;

import com.practice.spring.config.note.NoteConfiguration;
import com.practice.spring.exception.note.NoteLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(value = "dev")
public final class DevNoteLimitValidator implements NoteLimitValidator{

    private final NoteConfiguration noteConfiguration;

    @Autowired
    public DevNoteLimitValidator(NoteConfiguration noteConfiguration) {
        this.noteConfiguration = noteConfiguration;
    }

    @Override
    public void validate(Integer currentCountOfNotes) {
        if(currentCountOfNotes >= noteConfiguration.getLimit()) {
            throw new NoteLimitExceededException("Превышен лимит создания заметок");
        }
    }
}
