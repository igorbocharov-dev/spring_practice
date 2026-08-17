package com.practice.spring.util.validator.note;

import com.practice.spring.config.note.ImportNoteConfiguration;
import com.practice.spring.exception.note.ImportNoteException;
import com.practice.spring.util.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class ImportNotesLimitValidator implements Validator<Integer> {

    private final ImportNoteConfiguration importNoteConfiguration;

    @Autowired
    public ImportNotesLimitValidator(ImportNoteConfiguration importNoteConfiguration) {
        this.importNoteConfiguration = importNoteConfiguration;
    }

    @Override
    public void validate(Integer currentCount) {
        if(currentCount > importNoteConfiguration.getLimit()) {
            throw new ImportNoteException("Превышен лимит пакетного импорта заметок");
        }
    }
}
