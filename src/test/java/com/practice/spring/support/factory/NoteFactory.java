package com.practice.spring.support.factory;

import com.practice.spring.dto.note.*;
import com.practice.spring.entity.note.Note;
import com.practice.spring.entity.noteRevision.NoteRevision;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class NoteFactory {

    public static final String title = "test title";
    public static final String text = "test text";
    public static final String author = "Pavel Durov";

    public static Note note(){
        return new Note(title, text, author);
    }

    public static Note note(String author){
        return new Note(title, text, author);
    }

    public static Note note(Long id){
        Note note = new Note(title, text, author);
        note.setId(id);
        return note;
    }

    public static CreateNoteRequest createNoteRequest(){
        return new CreateNoteRequest(title, text);
    }

    public static UpdateNoteRequest updateNoteRequest(){
        return new UpdateNoteRequest(title + " update", text + " update");
    }

    public static LocationNoteResponse locationNoteResponse(Long id){
        return new LocationNoteResponse(URI.create("/notes/" + id));
    }

    public static NoteResponse noteResponse(){
        return new NoteResponse(title, text, author);
    }

    public static NoteResponse updatedNoteResponse(UpdateNoteRequest updateNoteRequest, String author){
        return new NoteResponse(updateNoteRequest.title(), updateNoteRequest.text(), author);
    }

    public static List<Note> notesByAuthor(String author, long size){
        List<Note> notes = new ArrayList<>();
        for (long i = 0; i < size; i++) {
            notes.add(note(author));
        }
        return notes;
    }

    public static ImportNotesRequest importNotesRequest(int size){
        List<CreateNoteRequest> createNoteRequestList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            createNoteRequestList.add(new CreateNoteRequest(title + i, text));
        }
        return new ImportNotesRequest(createNoteRequestList);
    }

    public static NoteRevision noteRevision(Note note){
        return new NoteRevision(note);
    }

    public static List<NoteRevision> noteRevisions(Note note1, Note note2, Note note3){
        return List.of(noteRevision(note1), noteRevision(note2), noteRevision(note3));
    }
}
