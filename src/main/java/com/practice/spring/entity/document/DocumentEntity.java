package com.practice.spring.entity.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Document
@Getter
@Setter
public class DocumentEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    private String body;

    private List<String> links;

    private DocumentStatus status;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public DocumentEntity() {
    }

    public DocumentEntity(String body, List<String> links, DocumentStatus status) {
        this.body = body;
        this.links = links;
        this.status = status;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        return getId() == null;
    }
}
