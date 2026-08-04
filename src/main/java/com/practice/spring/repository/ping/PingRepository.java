package com.practice.spring.repository.ping;

import com.practice.spring.dto.ping.PingStatus;
import org.springframework.stereotype.Repository;

import java.time.Clock;

@Repository
public interface PingRepository {

    PingStatus getPing(Clock clock);
}
