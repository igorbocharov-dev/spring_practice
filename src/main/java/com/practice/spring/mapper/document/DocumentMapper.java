package com.practice.spring.mapper.document;

import com.practice.spring.dto.document.DocumentResponse;
import com.practice.spring.entity.document.DocumentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    DocumentResponse toDocumentResponse(DocumentEntity entity);
}
