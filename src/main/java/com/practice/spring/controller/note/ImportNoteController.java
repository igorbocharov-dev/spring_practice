package com.practice.spring.controller.note;

import com.practice.spring.dto.note.ImportNotesRequest;
import com.practice.spring.service.note.ImportNoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notes/import")
public class ImportNoteController {

    private final ImportNoteService importNoteService;

    @Autowired
    public ImportNoteController(ImportNoteService importNoteService) {
        this.importNoteService = importNoteService;
    }

    @PostMapping
    public ResponseEntity<Void> importNotes(@RequestBody @Valid ImportNotesRequest request){
        importNoteService.importNotes(request);
        return ResponseEntity.noContent().build();
    }
}
