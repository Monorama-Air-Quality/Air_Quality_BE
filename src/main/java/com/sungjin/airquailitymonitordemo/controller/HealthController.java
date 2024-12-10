package com.sungjin.airquailitymonitordemo.controller;

import com.sungjin.airquailitymonitordemo.dto.request.HealthData.HealthDataRequestDto;
import com.sungjin.airquailitymonitordemo.service.HealthDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/health")
public class HealthController {
    private final HealthDataService healthDataService;

    @PostMapping("")
    public ResponseEntity<String> saveHealthData(@RequestBody HealthDataRequestDto healthData) {
        healthDataService.saveHealthData(healthData);
        return ResponseEntity.ok("Health data saved successfully");
    }
}
