package com.practice.spring.repository.ping;

import com.practice.spring.dto.ping.PingStatus;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

@Component(value = "PingRepository")
public class PingRepositoryImpl implements PingRepository {

    @Override
    public PingStatus getPing(Clock clock) {
        return new PingStatus(Instant.now(clock));
    }
}
