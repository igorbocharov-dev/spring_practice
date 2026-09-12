package com.practice.spring.service.document;

import com.practice.spring.dto.document.DocumentRequest;
import com.practice.spring.dto.document.DocumentResponse;
import com.practice.spring.entity.document.DocumentEntity;
import com.practice.spring.entity.document.DocumentStatus;
import com.practice.spring.mapper.document.DocumentMapper;
import com.practice.spring.repository.document.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentServiceImpl implements DocumentService{

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository, DocumentMapper documentMapper) {
        this.documentRepository = documentRepository;
        this.documentMapper = documentMapper;
    }

    @Override
    public DocumentResponse saveDocument(DocumentRequest request) {
        DocumentEntity entity = new DocumentEntity(request.body(), request.links(), DocumentStatus.NEW);
        DocumentEntity savedEntity = documentRepository.save(entity);
        return documentMapper.toDocumentResponse(savedEntity);
    }
}
