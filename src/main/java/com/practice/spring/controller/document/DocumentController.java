package com.practice.spring.controller.document;

import com.practice.spring.dto.document.DocumentRequest;
import com.practice.spring.dto.document.DocumentResponse;
import com.practice.spring.service.document.DocumentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DocumentController {

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/v1/document")
    public ResponseEntity<DocumentResponse> getSavedDocument(@RequestBody @Valid DocumentRequest documentRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(documentService.saveDocument(documentRequest));
    }
}
