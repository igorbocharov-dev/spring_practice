package com.practice.spring.util.validator.note.exporter;

import com.practice.spring.exception.note.IllegalNoteExportFormatException;
import com.practice.spring.exception.note.UnsupportedNoteExportFormatException;
import com.practice.spring.util.validator.Validator;
import org.springframework.stereotype.Component;

@Component
public final class ExportNoteFormatValidator implements Validator<String> {
    @Override
    public void validate(String format) {
        if(format == null || format.isBlank()) {
            throw new IllegalNoteExportFormatException("Формат экспорта не должен быть пустым");
        }
    }
}
