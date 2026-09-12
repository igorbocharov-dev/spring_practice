package com.practice.spring.repository.requestEvent;

import com.practice.spring.entity.requestEvent.RequestEvent;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestEventRepository {
    RequestEvent save(RequestEvent event);
}
