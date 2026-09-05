package com.practice.spring.repository.requestEvent;

import com.practice.spring.entity.requestEvent.RequestEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class RequestEventRepositoryImpl implements RequestEventRepository{

    private final JdbcClient jdbcClient;

    @Autowired
    public RequestEventRepositoryImpl(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public RequestEvent save(RequestEvent event){
        String errorTypeName = null;
        if(event.errorType() != null) {
            errorTypeName = event.errorType().name();
        }
        jdbcClient.sql("""
                INSERT INTO Notes_ClickHouse.request_events(
                event_time,
                doc_id,
                event_type,
                status,
                processing_ms,
                http_status,
                error_type)
                VALUES (
                :eventTime,
                :docId,
                :eventType,
                :status,
                :processingMs,
                :httpStatus,
                :errorType)
        """)
                .param("eventTime", event.eventTime())
                .param("docId", event.docId())
                .param("eventType", event.requestEventType().name())
                .param("status", event.status().name())
                .param("processingMs", event.processingMs())
                .param("httpStatus", event.httpStatus())
                .param("errorType", errorTypeName)
                .update();
        return event;
    }
}
