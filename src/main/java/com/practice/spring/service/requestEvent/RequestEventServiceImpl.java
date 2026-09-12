package com.practice.spring.service.requestEvent;

import com.practice.spring.dto.requestEvent.RequestEventSubject;
import com.practice.spring.entity.requestEvent.RequestEvent;
import com.practice.spring.repository.requestEvent.RequestEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Service
public class RequestEventServiceImpl implements RequestEventService{

    private final RequestEventRepository requestEventRepository;
    private final Clock clock;

    @Autowired
    public RequestEventServiceImpl(
            @Qualifier("requestEventRepositoryImpl") RequestEventRepository requestEventRepository,
            Clock clock) {
        this.requestEventRepository = requestEventRepository;
        this.clock = clock;
    }

    @Override
    public void create(RequestEventSubject subject) {
        requestEventRepository.save(new RequestEvent(
                Instant.now(clock),
                subject.docId(),
                subject.eventType(),
                subject.status(),
                subject.processingMs(),
                subject.httpStatus(),
                subject.errorType()
        ));
    }
}
