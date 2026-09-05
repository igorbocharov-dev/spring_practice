package com.practice.spring.service.requestEvent;

import com.practice.spring.dto.requestEvent.RequestEventSubject;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class RequestEventConsumer {

    private final EventQueue eventQueue;
    private final RequestEventService requestEventService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    public RequestEventConsumer(EventQueue eventQueue, RequestEventService requestEventService) {
        this.eventQueue = eventQueue;
        this.requestEventService = requestEventService;
    }

    @PostConstruct
    public void consumeEvent() {
        executorService.submit(() -> {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    RequestEventSubject subject = eventQueue.take();
                    requestEventService.create(subject);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e){
                    log.error("Failed to save request event to ClickHouse", e);
                }
            }
        });
    }

    @PreDestroy
    public void shoutDownNow(){
        executorService.shutdownNow();
    }
}
