package com.practice.spring.service.document.task;

import com.practice.spring.dto.requestEvent.RequestEventSubject;
import com.practice.spring.entity.document.DocumentEntity;
import com.practice.spring.entity.document.DocumentStatus;
import com.practice.spring.entity.requestEvent.RequestEventType;
import com.practice.spring.repository.document.DocumentRepository;
import com.practice.spring.service.requestEvent.EventQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class DocumentTaskManager {

    private final DocumentTaskProperties properties;
    private final MongoTemplate mongoTemplate;
    private final EventQueue eventQueue;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final DocumentRepository documentRepository;
    private final Clock clock;

    @Autowired
    public DocumentTaskManager(
            DocumentTaskProperties properties,
            MongoTemplate mongoTemplate,
            @Qualifier("requestEventQueue") EventQueue eventQueue,
            DocumentRepository documentRepository, Clock clock) {
        this.properties = properties;
        this.mongoTemplate = mongoTemplate;
        this.eventQueue = eventQueue;
        this.documentRepository = documentRepository;
        this.clock = clock;
    }

    private void batchUpdate(List<DocumentEntity> documents){
        for (int from = 0; from < documents.size(); from+=properties.getBatchUpdateLimit()) {
            int to = Math.min(from + properties.getBatchUpdateLimit(), documents.size());
            List<DocumentEntity> batch = documents.subList(from, to);
            updateMulti(batch);
        }
    }

    private void updateMulti(List<DocumentEntity> documents) {
        List<UUID> ids = documents.stream().map(DocumentEntity::getId).toList();
        mongoTemplate.updateMulti(
                Query.query(Criteria.where("_id").in(ids)),
                new Update()
                        .set("status", DocumentStatus.PROCESSED)
                        .set("updatedAt", Instant.now(clock)),
                DocumentEntity.class);
        for (DocumentEntity document : documents) {
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
        List<DocumentEntity> documents = documentRepository.findAllByStatus(DocumentStatus.NEW);
        executorService.submit(() -> batchUpdate(documents));
        executorService.submit(() -> batchUpdate(documents));
    }
}
