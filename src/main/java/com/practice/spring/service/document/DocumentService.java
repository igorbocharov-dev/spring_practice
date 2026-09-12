package com.practice.spring.service.document;

import com.practice.spring.dto.document.DocumentRequest;
import com.practice.spring.dto.document.DocumentResponse;

public interface DocumentService {

    DocumentResponse saveDocument(DocumentRequest request);
}
