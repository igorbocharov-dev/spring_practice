package com.practice.spring.support.factory;

import com.practice.spring.dto.note.CreateNoteRequest;
import com.practice.spring.dto.note.LocationNoteResponse;
import com.practice.spring.dto.note.NoteResponse;
import com.practice.spring.dto.note.UpdateNoteRequest;
import com.practice.spring.entity.note.Note;

import java.net.URI;

public class NoteFactory {

    public static final String title = "test title";
    public static final String body = "test body";

    public static Note note(Long id){
        Note note = new Note(title, body);
        note.setId(id);
        return note;
    }

    public static CreateNoteRequest createNoteRequest(){
        return new CreateNoteRequest(title, body);
    }

    public static UpdateNoteRequest updateNoteRequest(){
        return new UpdateNoteRequest(title + " update", body + " update");
    }

    public static LocationNoteResponse locationNoteResponse(Long id){
        return new LocationNoteResponse(URI.create("/notes/" + id));
    }

    public static NoteResponse noteResponse(){
        return new NoteResponse(title, body);
    }

    public static NoteResponse updatedNoteResponse(UpdateNoteRequest updateNoteRequest){
        return new NoteResponse(updateNoteRequest.title(), updateNoteRequest.body());
    }
}
