package com.practice.spring.controller.note;

import com.practice.spring.dto.note.ExportNotesResult;
import com.practice.spring.service.note.exporter.ExportNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notes/export")
public class ExportNoteController {

    private final ExportNoteService exportNoteService;

    @Autowired
    public ExportNoteController(ExportNoteService exportNoteService) {
        this.exportNoteService = exportNoteService;
    }

    @GetMapping
    public ResponseEntity<byte []> exportNotes(@RequestParam ("format") String format){
        ExportNotesResult exportNotesResult = exportNoteService.export(format);
        return ResponseEntity.ok()
                .contentType(exportNotesResult.contentType())
                .body(exportNotesResult.data());
    }
}
