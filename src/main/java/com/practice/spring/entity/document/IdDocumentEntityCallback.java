package com.practice.spring.entity.document;

import org.springframework.core.Ordered;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;

import java.util.UUID;

public class IdDocumentEntityCallback implements BeforeConvertCallback<DocumentEntity>, Ordered {

    @Override
    public DocumentEntity onBeforeConvert(DocumentEntity entity, String collection) {
        if(entity.isNew()){
            entity.setId(UUID.randomUUID());
        }
        return entity;
    }

    @Override
    public int getOrder() {
        return 101;
    }
}
