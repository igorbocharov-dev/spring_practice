package com.practice.spring.service.noteEventLog;

import com.practice.spring.entity.noteEventLog.NoteEventLog;
import com.practice.spring.event.NoteEvent;
import com.practice.spring.repository.noteEventLog.NoteEventLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class NoteEventLogService {

    private final NoteEventLogRepository noteEventLogRepository;
    private final Clock clock;

    @Autowired
    public NoteEventLogService(NoteEventLogRepository noteEventLogRepository, Clock clock) {
        this.noteEventLogRepository = noteEventLogRepository;
        this.clock = clock;
    }

    @Transactional
    public void save(NoteEvent event){
        if(noteEventLogRepository.existsByNoteId(event.noteId())){
            return;
        }
        noteEventLogRepository.save(new NoteEventLog(
                event.noteId(),
                event.author(),
                event.eventId(),
                event.type(),
                event.occurredAt(),
                Instant.now(clock)));
    }
}
