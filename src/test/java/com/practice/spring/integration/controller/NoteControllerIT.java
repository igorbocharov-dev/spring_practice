package com.practice.spring.integration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.spring.dto.note.CreateNoteRequest;
import com.practice.spring.dto.note.NoteResponse;
import com.practice.spring.dto.note.UpdateNoteRequest;
import com.practice.spring.dto.noteRevision.NoteRevisionResponse;
import com.practice.spring.dto.paging.SliceResponse;
import com.practice.spring.entity.note.Note;
import com.practice.spring.entity.noteRevision.NoteRevision;
import com.practice.spring.repository.note.NoteRepository;
import com.practice.spring.repository.note.NoteRevisionRepository;
import com.practice.spring.support.config.AbstractSpringBootIT;
import com.practice.spring.support.factory.NoteFactory;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class NoteControllerIT extends AbstractSpringBootIT {

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private NoteRevisionRepository noteRevisionRepository;
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanUpDb(){
        jdbcTemplate.execute("TRUNCATE TABLE note, note_revision RESTART IDENTITY CASCADE");
    }


    @Test
    void shouldCreateEntityAndReturnLocation() throws Exception {
        CreateNoteRequest request = NoteFactory.createNoteRequest();
        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/notes/create")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(status().isCreated())
                .andReturn();

        Note createdNote = noteRepository.findAll().getFirst();

        assertThat(createdNote.getTitle()).isEqualTo(request.title());
        assertThat(createdNote.getText()).isEqualTo(request.text());
        assertThat(createdNote.getAuthor()).isEqualTo(request.author());

        String location = result.getResponse().getHeader(HttpHeaders.LOCATION);

        assertThat(location).isEqualTo("/notes/" + createdNote.getId());
    }

    @Test
    void shouldCreateNoteRevisionAndIncrementVersion() throws Exception{
        Note note = noteRepository.save(NoteFactory.note());
        Long id = note.getId();

        UpdateNoteRequest updateNoteRequest = NoteFactory.updateNoteRequest();
        String jsonRequest = objectMapper.writeValueAsString(updateNoteRequest);

        MvcResult result = mockMvc.perform(put("/notes/update/{id}", id)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$.title").exists(),
                        jsonPath("$.text").exists(),
                        jsonPath("$.author").exists())
                .andExpect(status().isOk())
                .andReturn();

        NoteRevision noteRevision = noteRevisionRepository.findAll().getFirst();
        assertThat(noteRevision.getNote().getId()).isEqualTo(note.getId());
        assertThat(noteRevision.getOldTitle()).isEqualTo(note.getTitle());
        assertThat(noteRevision.getOldText()).isEqualTo(note.getText());

        assertThat(noteRepository.findAll().getFirst().getVersion()).isEqualTo(1);

        String jsonResult = result.getResponse().getContentAsString();
        NoteResponse response = objectMapper.readValue(jsonResult, NoteResponse.class);

        assertThat(response.title()).isEqualTo(updateNoteRequest.title());
        assertThat(response.text()).isEqualTo(updateNoteRequest.text());
    }

    @Test
    void shouldExecuteInOneRequest() throws Exception {
        int page = 0;
        int size = 3;

        Note note1 = NoteFactory.note();
        Note note2 = NoteFactory.note();
        Note note3 = NoteFactory.note();

        noteRepository.save(note1);
        noteRepository.save(note2);
        noteRepository.save(note3);

        List<NoteRevision> noteRevisions = noteRevisionRepository.saveAll(NoteFactory.noteRevisions(note1, note2, note3));
        List<Long> notesIds = noteRevisions.stream().map(noteRevision -> noteRevision.getNote().getId()).toList();

        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();

        MvcResult result = mockMvc.perform(get("/notes/history")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)))
                .andExpectAll(
                        jsonPath("$.content[*].noteId").exists(),
                        jsonPath("$.content[*].oldTitle").exists(),
                        jsonPath("$.content[*].oldText").exists(),
                        jsonPath("$.content[*].changedAt").exists(),
                        jsonPath("$.hasNext").exists())
                .andExpect(status().isOk())
                .andReturn();

        assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);

        String jsonResult = result.getResponse().getContentAsString();
        SliceResponse<NoteRevisionResponse> sliceResponse = objectMapper.readValue(jsonResult, new TypeReference<>() {});
        List<NoteRevisionResponse> content = sliceResponse.content();

        assertThat(content.size()).isEqualTo(size);
        assertFalse(sliceResponse.hasNext());

        assertThat(content).extracting(NoteRevisionResponse::noteId).containsExactlyInAnyOrderElementsOf(notesIds);
        assertThat(content).extracting(NoteRevisionResponse::oldTitle).allMatch(title -> title.equals(note1.getTitle()));
        assertThat(content).extracting(NoteRevisionResponse::oldText).allMatch(text -> text.equals(note1.getText()));
    }
}
