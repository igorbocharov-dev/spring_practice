package com.practice.spring.controller.note;

import com.practice.spring.dto.note.CreateNoteRequest;
import com.practice.spring.dto.note.NoteResponse;
import com.practice.spring.dto.note.NotesResponse;
import com.practice.spring.dto.note.UpdateNoteRequest;
import com.practice.spring.service.note.NoteService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createNote(@RequestBody @Valid CreateNoteRequest createNoteRequest){
        log.info("Request to create note with title: {} ; and body: {}", createNoteRequest.title(), createNoteRequest.body());
        return ResponseEntity.created(noteService.createNote(createNoteRequest).location()).build();
    }

    @GetMapping
    public ResponseEntity<NotesResponse> getNotes(){
        return ResponseEntity.ok(noteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getNoteById(@PathVariable Long id){
        return ResponseEntity.ok(noteService.findById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<NoteResponse> updateNote(@PathVariable Long id, @RequestBody @Valid UpdateNoteRequest updateNoteRequest){
        return ResponseEntity.ok(noteService.update(id, updateNoteRequest));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteNote(@PathVariable Long id){
        noteService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
