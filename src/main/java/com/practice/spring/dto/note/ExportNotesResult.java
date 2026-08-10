package com.practice.spring.dto.note;

import org.springframework.http.MediaType;

public record ExportNotesResult(byte [] data, MediaType contentType){
}
