package com.practice.spring.repository.document;

import com.practice.spring.entity.document.DocumentEntity;
import com.practice.spring.entity.document.DocumentStatus;
import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends MongoRepository<DocumentEntity, UUID> {
    @Query(value = "{'status': ?0}", fields = "{'_id': 1}")
    List<UUID> findIdsByStatus(DocumentStatus status, Limit limit);
}
