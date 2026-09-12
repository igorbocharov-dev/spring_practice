package com.practice.spring.support.factory.document;

import com.practice.spring.dto.document.DocumentRequest;

import java.util.List;

public class DocumentFactory {

    public static DocumentRequest documentRequest(){
        return new DocumentRequest("some body", List.of("link1", "link2", "link3"));
    }
}
