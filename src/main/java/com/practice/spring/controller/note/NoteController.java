package com.practice.spring.controller.note;

import com.practice.spring.dto.note.*;
import com.practice.spring.dto.noteRevision.NoteRevisionResponse;
import com.practice.spring.dto.paging.SliceResponse;
import com.practice.spring.service.note.NoteService;
import com.practice.spring.service.noteRevision.NoteRevisionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;
    private final NoteRevisionService noteRevisionService;

    @Autowired
    public NoteController(NoteService noteService, NoteRevisionService noteRevisionService) {
        this.noteService = noteService;
        this.noteRevisionService = noteRevisionService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createNote(@AuthenticationPrincipal String author, @RequestBody @Valid CreateNoteRequest createNoteRequest){
        log.info("Request to create note with title: {} ; and text: {}", createNoteRequest.title(), createNoteRequest.text());
        return ResponseEntity.created(noteService.create(author, createNoteRequest).location()).build();
    }

    @GetMapping
    public ResponseEntity<NotesResponse> getNotes(){
        return ResponseEntity.ok(noteService.findAll());
    }

    @GetMapping("/history")
    public ResponseEntity<SliceResponse<NoteRevisionResponse>> historyOfNotes(
            @RequestParam(name = "page") @Min(value = 0) int page,
            @RequestParam(name = "size") @Min(value = 0) @Max(value = 50) int size)
    {
        return ResponseEntity.ok(noteRevisionService.getAllHistory(page, size));
    }

    @GetMapping("/stats")
    public ResponseEntity<AuthorNoteSummary> summaryOfAuthor(@RequestParam("author") String author){
        return ResponseEntity.ok(noteService.authorNoteSummary(author));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getNoteById(@PathVariable Long id){
        return ResponseEntity.ok(noteService.getNoteResponseById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<NoteResponse> updateNote(@AuthenticationPrincipal String author, @PathVariable Long id, @RequestBody @Valid UpdateNoteRequest updateNoteRequest){
        return ResponseEntity.ok(noteService.update(id, author, updateNoteRequest));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteNote(@AuthenticationPrincipal String author, @PathVariable Long id){
        noteService.delete(id, author);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
