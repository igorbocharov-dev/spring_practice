package com.practice.spring.service.requestEvent;

import com.practice.spring.dto.requestEvent.RequestEventSubject;

public interface EqSubscribePoint {
    RequestEventSubject take() throws InterruptedException;
}
