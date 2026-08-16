package com.practice.spring.integration.service;

import com.practice.spring.dto.note.AuthorNoteSummary;
import com.practice.spring.dto.note.UpdateNoteRequest;
import com.practice.spring.entity.note.Note;
import com.practice.spring.repository.note.NoteRepository;
import com.practice.spring.service.note.NoteService;
import com.practice.spring.support.config.AbstractSpringBootIT;
import com.practice.spring.support.factory.NoteFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
public class NoteServiceIT extends AbstractSpringBootIT {

    @Autowired
    @MockitoSpyBean
    private NoteRepository noteRepository;
    @Autowired
    private NoteService noteService;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @BeforeEach
    void cleanUpDb(){
        jdbcTemplate.execute("TRUNCATE TABLE note, note_revision RESTART IDENTITY CASCADE");
    }

    @Test
    void update_ShouldDemonstrationOptimisticLocking() throws InterruptedException, ExecutionException {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        CountDownLatch latch = new CountDownLatch(2);

        Note savedNote = noteRepository.save(NoteFactory.note());
        Long id = savedNote.getId();

        UpdateNoteRequest updateNoteRequest1 = new UpdateNoteRequest("title", "First update");
        UpdateNoteRequest updateNoteRequest2 = new UpdateNoteRequest("title", "Second update");

        Runnable task1 = () -> transactionTemplate.execute(status -> {
            latch.countDown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            noteService.update(id, updateNoteRequest1);
            return null;
        });

        Runnable task2 = () -> transactionTemplate.execute(status -> {
            latch.countDown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            noteService.update(id, updateNoteRequest2);
            return null;
        });

        try (ExecutorService executorService = Executors.newFixedThreadPool(2)) {

            Future<?> result1 = executorService.submit(task1);
            Future<?> result2 = executorService.submit(task2);

            int success = 0;
            int failure = 0;

            for (Future<?> future : List.of(result1, result2)) {
                try {
                    future.get();
                    success++;
                } catch (ExecutionException e) {
                    failure++;
                    assertInstanceOf(ObjectOptimisticLockingFailureException.class, e.getCause());
                }
            }

            assertEquals(1, success);
            assertEquals(1, failure);

            Note updatedNote = noteRepository.findById(id).orElseThrow();

            assertTrue(updatedNote.getText().equals(updateNoteRequest1.text()) ||
                    updatedNote.getText().equals(updateNoteRequest2.text()));
        }

    }

    @Test
    void authorNoteSummary_ShouldDemoCacheable(){
        String author = "Ivan";
        long size = 10;
        List<Note> notes = NoteFactory.notesByAuthor(author, size);
        noteRepository.saveAll(notes);

        clearInvocations(noteRepository);

        AuthorNoteSummary result1 = noteService.authorNoteSummary(author);
        assertNotNull(result1);
        log.info("Author summary: count notes - {}, date of last note: {}",
                result1.countOfPersonalNotes(), result1.dateOfLastNote());

        AuthorNoteSummary result2 = noteService.authorNoteSummary(author);
        assertNotNull(result2);
        log.info("Author summary: count notes - {}, date of last note: {}",
                result1.countOfPersonalNotes(), result1.dateOfLastNote());

        assertSame(result1, result2);

        verify(noteRepository, times(1)).findLastCreatedAtByAuthor(author);
        verify(noteRepository, times(1)).countByAuthor(author);
    }

}
