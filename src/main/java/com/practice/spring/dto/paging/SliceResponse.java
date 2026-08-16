package com.practice.spring.dto.paging;

import java.util.List;

public record SliceResponse<T> (List<T> content, Boolean hasNext) {}