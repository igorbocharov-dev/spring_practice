package com.practice.spring.service.requestEvent;

import com.practice.spring.dto.requestEvent.RequestEventSubject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
@Slf4j
public class RequestEventQueue implements EventQueue {

    private final BlockingQueue<RequestEventSubject> queue = new ArrayBlockingQueue<>(1000);

    @Override
    public void offer(RequestEventSubject subject) {
        if(!queue.offer(subject)){
            log.warn("Request event queue is full, wait and do it again");
        }
    }

    @Override
    public RequestEventSubject take() throws InterruptedException {
        return queue.take();
    }

}
