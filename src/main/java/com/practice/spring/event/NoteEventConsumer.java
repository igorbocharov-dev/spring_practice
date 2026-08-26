package com.practice.spring.event;

import com.practice.spring.service.noteEventLog.NoteEventLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NoteEventConsumer {

    private final NoteEventLogService noteEventLogService;

    @Autowired
    public NoteEventConsumer(NoteEventLogService noteEventLogService) {
        this.noteEventLogService = noteEventLogService;
    }

    @KafkaListener(topics = "note-events", groupId = "note_event_log")
    public void consume(NoteEvent event) {
        noteEventLogService.save(event);
    }
}
