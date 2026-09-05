package com.practice.spring.service.requestEvent;

import com.practice.spring.dto.requestEvent.RequestEventSubject;

public interface EqPublishPoint {
    void offer(RequestEventSubject subject);
}
