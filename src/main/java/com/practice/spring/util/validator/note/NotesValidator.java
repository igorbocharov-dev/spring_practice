package com.practice.spring.util.validator.note;

import com.practice.spring.entity.note.Note;
import com.practice.spring.exception.note.NoteNotFoundException;
import com.practice.spring.util.validator.Validator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class NotesValidator implements Validator<List<Note>> {

    @Override
    public void validate(List<Note> notes) {
        if(notes == null || notes.isEmpty()){
            throw new NoteNotFoundException("Нет заметок для экспорта. Попробуйте создать новые.");
        }
    }
}
