package com.practice.spring.util.validator.note;

import com.practice.spring.exception.note.ImportNoteException;
import com.practice.spring.util.validator.Validator;
import org.springframework.stereotype.Component;

@Component
public final class ImportNotesLimitValidator implements Validator<Integer> {
    @Override
    public void validate(Integer currentCount) {
        if(currentCount > 2) {
            throw new ImportNoteException("Превышен лимит пакетного импорта заметок");
        }
    }
}
