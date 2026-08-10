package com.practice.spring.util.validator.note.exporter;

import com.practice.spring.exception.note.UnsupportedNoteExportFormatException;
import com.practice.spring.service.note.exporter.NoteExporter;
import com.practice.spring.util.validator.Validator;
import org.springframework.stereotype.Component;

@Component
public final class NoteExporterValidator implements Validator<NoteExporter> {
    @Override
    public void validate(NoteExporter exporter) {
        if(exporter == null){
            throw new UnsupportedNoteExportFormatException("Не допустимый формат экспорта");
        }
    }
}
