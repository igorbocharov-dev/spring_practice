package com.practice.spring.service.ping;

import com.practice.spring.dto.ping.PingStatus;
import com.practice.spring.repository.ping.PingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class PingServiceImpl implements PingService{

    private final PingRepository pingRepository;
    private final Clock clock;

    @Autowired
    public PingServiceImpl(@Qualifier(value = "PingRepository") PingRepository pingRepository, Clock clock) {
        this.pingRepository = pingRepository;
        this.clock = clock;
    }

    @Override
    public PingStatus getPingStatus(){
        return pingRepository.getPing(clock);
    }
}
