package com.practice.spring.util.validator.note;

import com.practice.spring.entity.note.Note;
import com.practice.spring.exception.note.NoteNotFoundException;
import com.practice.spring.util.validator.Validator;
import org.springframework.stereotype.Component;

@Component
public final class NoteValidator implements Validator<Note> {

    @Override
    public void validate(Note note) {
        if(note == null){
            throw new NoteNotFoundException("Значение note равно 'null'");
        }
    }
}
