package com.practice.spring.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NoteEventProducer {

    private final KafkaTemplate<String, NoteEvent> template;

    @Autowired
    public NoteEventProducer(KafkaTemplate<String, NoteEvent> template) {
        this.template = template;
    }

    public void send(NoteEvent event){
        template.send("note-events", event.noteId().toString(), event);
    }
}
