package com.practice.spring.integration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.spring.dto.note.CreateNoteRequest;
import com.practice.spring.dto.note.NoteResponse;
import com.practice.spring.dto.note.UpdateNoteRequest;
import com.practice.spring.dto.noteRevision.NoteRevisionResponse;
import com.practice.spring.dto.paging.SliceResponse;
import com.practice.spring.entity.note.Note;
import com.practice.spring.entity.noteEventLog.NoteEventLog;
import com.practice.spring.entity.noteRevision.NoteRevision;
import com.practice.spring.event.EventType;
import com.practice.spring.event.NoteEvent;
import com.practice.spring.event.NoteEventProducer;
import com.practice.spring.repository.note.NoteRepository;
import com.practice.spring.repository.note.NoteRevisionRepository;
import com.practice.spring.repository.noteEventLog.NoteEventLogRepository;
import com.practice.spring.security.user.Authority;
import com.practice.spring.support.config.AbstractSpringBootIT;
import com.practice.spring.support.factory.note.NoteFactory;
import com.practice.spring.support.factory.security.WithMockUserPrincipal;
import jakarta.persistence.EntityManagerFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.kafka.KafkaContainer;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class NoteControllerIT extends AbstractSpringBootIT {

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private NoteRevisionRepository noteRevisionRepository;
    @Autowired
    private NoteEventLogRepository noteEventLogRepository;
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private KafkaContainer kafkaContainer;

    private KafkaConsumer<String, NoteEvent> consumer;

    @Autowired
    private NoteEventProducer producer;

    @Autowired
    private Clock clock;

    @BeforeEach
    void setUpKafkaConsumer(){
        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "note-controller-it-" + UUID.randomUUID());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, NoteEvent.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.practice.spring");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(List.of("note-events"));
        consumer.poll(Duration.ofSeconds(1));
    }

    @AfterEach
    void tearDownKafkaConsumer(){
        if(consumer!=null){
            consumer.close();
        }
    }

    @BeforeEach
    void cleanUpDb(){
        jdbcTemplate.execute("TRUNCATE TABLE note, note_revision, note_event_log RESTART IDENTITY CASCADE");
    }


    @Test
    @WithMockUserPrincipal(authorities = Authority.WRITE)
    void shouldCreateEntityAndReturnLocation() throws Exception {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CreateNoteRequest request = NoteFactory.createNoteRequest();
        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/notes/create")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(status().isCreated())
                .andReturn();

        Note createdNote = noteRepository.findAll().getFirst();

        assertThat(createdNote.getAuthor()).isEqualTo(username);
        assertThat(createdNote.getTitle()).isEqualTo(request.title());
        assertThat(createdNote.getText()).isEqualTo(request.text());

        String location = result.getResponse().getHeader(HttpHeaders.LOCATION);

        assertThat(location).isEqualTo("/notes/" + createdNote.getId());

        await().atMost(Duration.ofSeconds(3)).untilAsserted(() -> {
            ConsumerRecords<String, NoteEvent> records = consumer.poll(Duration.ofMillis(200));

            assertThat(records).anyMatch(record -> {
                NoteEvent event = record.value();
                return record.key().equals(createdNote.getId().toString())
                        && event.noteId().equals(createdNote.getId())
                        && event.author().equals(username)
                        && event.type().equals(EventType.CREATED);
            });

            NoteEventLog eventLog = noteEventLogRepository.findAll().getFirst();
            assertThat(eventLog).isNotNull();
            assertThat(eventLog.getNoteId()).isEqualTo(createdNote.getId());
            assertThat(eventLog.getAuthor()).isEqualTo(createdNote.getAuthor());
            assertThat(eventLog.getType()).isEqualTo(EventType.CREATED);
        });
    }

    @Test
    void shouldIgnoreDuplicateEvent(){
        UUID uuid = UUID.randomUUID();
        NoteEvent event = new NoteEvent(1L, "John Preston",
                uuid, EventType.CREATED, Instant.now(clock));

        producer.send(event);
        await().atMost(Duration.ofSeconds(2)).untilAsserted(()->
                assertThat(noteEventLogRepository.findAll().size()).isEqualTo(1));

        producer.send(event);
        await().atMost(Duration.ofSeconds(2)).untilAsserted(()->
                assertThat(noteEventLogRepository.findAll().size()).isEqualTo(1));

       NoteEventLog eventLog = noteEventLogRepository.findAll().getFirst();

       assertThat(eventLog).isNotNull();
       assertThat(eventLog.getNoteId()).isEqualTo(event.noteId());
       assertThat(eventLog.getEventId()).isEqualTo(event.eventId());
       assertThat(eventLog.getAuthor()).isEqualTo(event.author());
       assertThat(eventLog.getType()).isEqualTo(event.type());
    }

    @Test
    @WithMockUserPrincipal(authorities = Authority.WRITE)
    void shouldCreateNoteRevisionAndIncrementVersion() throws Exception {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Note note = noteRepository.save(NoteFactory.note(username));
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

        assertThat(response.author()).isEqualTo(username);
        assertThat(response.title()).isEqualTo(updateNoteRequest.title());
        assertThat(response.text()).isEqualTo(updateNoteRequest.text());
    }

    @Test
    @WithMockUserPrincipal(authorities = Authority.ADMIN)
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
