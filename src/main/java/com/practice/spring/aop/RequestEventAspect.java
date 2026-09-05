package com.practice.spring.aop;

import com.practice.spring.dto.document.DocumentResponse;
import com.practice.spring.dto.requestEvent.RequestEventSubject;
import com.practice.spring.entity.document.DocumentStatus;
import com.practice.spring.entity.requestEvent.RequestEventType;
import com.practice.spring.error.ErrorType;
import com.practice.spring.service.requestEvent.EventQueue;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RequestEventAspect {

    private final EventQueue eventQueue;

    @Autowired
    public RequestEventAspect(@Qualifier("requestEventQueue") EventQueue eventQueue) {
        this.eventQueue = eventQueue;
    }

    @Pointcut("execution(* com.practice.spring.controller.document.DocumentController.getSavedDocument(..))")
    private void getSavedDocumentPointCut() {
    }

    @Around("getSavedDocumentPointCut()")
    public Object createRequestEventSubjectInQueueFromSaveDocument(ProceedingJoinPoint joinPoint) throws Throwable {
        ResponseEntity<DocumentResponse> response = null;
        long start = System.nanoTime();
        try {
            response = (ResponseEntity<DocumentResponse>) joinPoint.proceed();
            return response;
        } catch (Throwable e) {
            eventQueue.offer(new RequestEventSubject(
                    null,
                    RequestEventType.REQUEST_FAILED,
                    DocumentStatus.NEW,
                    duration(start),
                    null,
                    ErrorType.REQUEST_EVENT_FAILED_ERROR));
            throw e;
        } finally {
            if (response != null && response.getBody() != null) {
                DocumentResponse documentResponse = response.getBody();
                eventQueue.offer(new RequestEventSubject(
                        documentResponse.id(),
                        RequestEventType.REQUEST_RECEIVED,
                        documentResponse.status(),
                        duration(start),
                        response.getStatusCode().value(),
                        null));
            }
        }
    }

    private long duration(long start) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    }
}
