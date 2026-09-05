package com.practice.spring.service.document.task;

import com.practice.spring.dto.requestEvent.RequestEventSubject;
import com.practice.spring.entity.document.DocumentEntity;
import com.practice.spring.entity.document.DocumentStatus;
import com.practice.spring.entity.requestEvent.RequestEventType;
import com.practice.spring.repository.document.DocumentRepository;
import com.practice.spring.service.requestEvent.EventQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class DocumentTaskManager {

    private final MongoTemplate mongoTemplate;
    private final EventQueue eventQueue;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Autowired
    public DocumentTaskManager(
            MongoTemplate mongoTemplate,
            @Qualifier("requestEventQueue") EventQueue eventQueue) {
        this.mongoTemplate = mongoTemplate;
        this.eventQueue = eventQueue;
    }

    private void batchUpdate(int limit){
        for (int i = 0; i < limit; i++) {
            DocumentEntity document = mongoTemplate.findAndModify(
                    Query.query(Criteria.where("status").is(DocumentStatus.NEW)),
                    new Update().set("status", DocumentStatus.PROCESSED),
                    FindAndModifyOptions.options().returnNew(true),
                    DocumentEntity.class);
            if(document == null){
                break;
            }
            eventQueue.offer(new RequestEventSubject(
                    document.getId(),
                    RequestEventType.STATUS_CHANGED,
                    document.getStatus(),
                    null,
                    null,
                    null));
        }
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void job(){
        executorService.submit(() -> batchUpdate(5));
        executorService.submit(() -> batchUpdate(5));
    }
}
