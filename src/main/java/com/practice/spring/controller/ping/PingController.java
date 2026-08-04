package com.practice.spring.controller.ping;

import com.practice.spring.dto.ping.PingStatus;
import com.practice.spring.service.ping.PingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    private final PingService pingService;

    @Autowired
    public PingController(PingService pingService) {
        this.pingService = pingService;
    }

    @GetMapping("/ping")
    public ResponseEntity<PingStatus> ping(){
        return ResponseEntity.ok(pingService.getPingStatus());
    }
}
