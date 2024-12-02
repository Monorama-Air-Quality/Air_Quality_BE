package com.sungjin.airquailitymonitordemo.controller;

import com.sungjin.airquailitymonitordemo.dto.request.SensorDataRequestDto;
import com.sungjin.airquailitymonitordemo.dto.response.SensorDataBatchResponseDto;
import com.sungjin.airquailitymonitordemo.dto.response.SensorDataResponseDto;
import com.sungjin.airquailitymonitordemo.service.SensorDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sensor-data")
@Slf4j
@RequiredArgsConstructor
public class SensorDataController {
    private final SensorDataService sensorDataService;

    @PostMapping("/batch")
    public ResponseEntity<SensorDataBatchResponseDto> uploadBatchData(
            @RequestBody List<SensorDataRequestDto> dataList) {
        log.info("Received batch sensor data upload request with {} entries", dataList.size());
        try {
            int processedCount = sensorDataService.processBatchData(dataList);
            return ResponseEntity.ok(new SensorDataBatchResponseDto(
                    true,
                    String.format("Successfully processed %d sensor data entries", processedCount),
                    processedCount
            ));
        } catch (Exception e) {
            log.error("Error processing batch sensor data", e);
            return ResponseEntity.ok(new SensorDataBatchResponseDto(
                    false,
                    "Error processing sensor data: " + e.getMessage(),
                    0
            ));
        }
    }

    @GetMapping("/history/{deviceId}")
    public ResponseEntity<Page<SensorDataResponseDto>> getDeviceSensorHistory(
            @PathVariable String deviceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("timestamp").descending());
            Page<SensorDataResponseDto> history = sensorDataService.getDeviceSensorHistory(
                    deviceId, startDate, endDate, pageRequest);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Error fetching device sensor history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
