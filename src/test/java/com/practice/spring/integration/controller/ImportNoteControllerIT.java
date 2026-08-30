package com.practice.spring.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.spring.dto.note.ImportNotesRequest;
import com.practice.spring.error.ApiErrorResponse;
import com.practice.spring.error.ErrorType;
import com.practice.spring.repository.note.NoteRepository;
import com.practice.spring.security.user.Authority;
import com.practice.spring.support.config.AbstractSpringBootIT;
import com.practice.spring.support.factory.note.NoteFactory;
import com.practice.spring.support.factory.security.WithMockUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ImportNoteControllerIT extends AbstractSpringBootIT {

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private Clock clock;

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
    @WithMockUserPrincipal(authorities = Authority.WRITE)
    void shouldDontCreatedRecords_WhenImportWasExceededLimit() throws Exception{
        ImportNotesRequest request = NoteFactory.importNotesRequest(3);
        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/notes/import")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$.errorCode").exists(),
                        jsonPath("$.message").exists(),
                        jsonPath("$.status").exists(),
                        jsonPath("$.currentTime").exists()
                ).andExpect(status().isConflict()).andReturn();

        assertThat(noteRepository.findAll().size()).isZero();

        String jsonResult = result.getResponse().getContentAsString();
        ApiErrorResponse apiErrorResponse = objectMapper.readValue(jsonResult, ApiErrorResponse.class);

        assertThat(apiErrorResponse.errorCode()).isEqualTo(ErrorType.IMPORT_NOTES_ERROR.name());
        assertThat(apiErrorResponse.message()).isNotBlank();
        assertThat(apiErrorResponse.status()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(apiErrorResponse.currentTime())
                .isBetween(Instant.now(clock).minus(1, ChronoUnit.MINUTES), Instant.now(clock));

    }
}
