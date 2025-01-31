package com.sungjin.airquailitymonitordemo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sungjin.airquailitymonitordemo.dto.request.HealthData.HealthDataRequestDto;
import com.sungjin.airquailitymonitordemo.service.HealthService;
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
    private final HealthService healthService;

    @PostMapping
    public ResponseEntity<String> saveHealthData(
        @RequestBody(required = true) String requestBody  // String으로 받아서
    ) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            
        HealthDataRequestDto healthData = mapper.readValue(requestBody, HealthDataRequestDto.class);

        healthService.saveHealthData(healthData.userInfo(), healthData.measurements());
        return ResponseEntity.ok("Health data saved successfully");
    }
}
