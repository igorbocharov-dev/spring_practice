package com.practice.spring.service.requestEvent;

import com.practice.spring.dto.requestEvent.RequestEventSubject;

public interface RequestEventService {
    void create(RequestEventSubject subject);
}
