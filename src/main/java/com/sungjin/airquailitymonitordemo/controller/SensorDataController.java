package com.sungjin.airquailitymonitordemo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sungjin.airquailitymonitordemo.dto.request.SensorDataRequestDto;
import com.sungjin.airquailitymonitordemo.dto.response.SensorDataBatchResponseDto;
import com.sungjin.airquailitymonitordemo.dto.response.SensorDataResponseDto;
import com.sungjin.airquailitymonitordemo.service.SensorDataService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sensor-data")
@Slf4j
@RequiredArgsConstructor
public class SensorDataController {
    private final SensorDataService sensorDataService;
    private List<SensorDataRequestDto> dataList;
    private final ObjectMapper objectMapper;


//    @PostMapping("/batch")
//    public ResponseEntity<SensorDataBatchResponseDto> uploadBatchData(@RequestBody String rawData) {
//        log.info("Received raw batch sensor data upload request");
//        try {
//            // JSON 문자열을 수동으로 파싱
//            ObjectMapper objectMapper = new ObjectMapper();
//            List<SensorDataRequestDto> dataList = objectMapper.readValue(
//                    rawData, objectMapper.getTypeFactory().constructCollectionType(List.class, SensorDataRequestDto.class)
//            );
//
//            log.info("Parsed {} entries from raw batch data", dataList.size());
//
//            int processedCount = sensorDataService.processBatchData(dataList);
//
//            return ResponseEntity.ok(new SensorDataBatchResponseDto(
//                    true,
//                    String.format("Successfully processed %d sensor data entries", processedCount),
//                    processedCount
//            ));
//        } catch (EntityNotFoundException e) {
//            log.error("Project not found in batch processing", e);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new SensorDataBatchResponseDto(
//                            false,
//                            "Project not found: " + e.getMessage(),
//                            0
//                    ));
//        } catch (IOException e) {
//            log.error("Error parsing raw batch data", e);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(new SensorDataBatchResponseDto(
//                            false,
//                            "Invalid JSON format: " + e.getMessage(),
//                            0
//                    ));
//        } catch (Exception e) {
//            log.error("Error processing batch sensor data", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new SensorDataBatchResponseDto(
//                            false,
//                            "Error processing sensor data: " + e.getMessage(),
//                            0
//                    ));
//        }
//    }



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

    @PostMapping("/test")
    public ResponseEntity<String> testEndpoint(@RequestBody SensorDataRequestDto dto) {
        log.info("Received: {}", dto);
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/batch")
    public ResponseEntity<SensorDataBatchResponseDto> uploadBatchData(@RequestBody String rawData) {
        log.info("Received raw batch sensor data upload request");
        try {
            int processedCount = sensorDataService.processBatchData(rawData);
            return ResponseEntity.ok(new SensorDataBatchResponseDto(
                    true,
                    String.format("Successfully processed %d sensor data entries", processedCount),
                    processedCount
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new SensorDataBatchResponseDto(false, e.getMessage(), 0));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SensorDataBatchResponseDto(false, e.getMessage(), 0));
        } catch (Exception e) {
            log.error("Error processing batch sensor data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SensorDataBatchResponseDto(false, "Internal server error", 0));
        }
    }

    private byte[] convertRawData(JsonNode rawArray) {
        if (rawArray == null || !rawArray.isArray()) {
            return new byte[0];
        }

        byte[] rawData = new byte[rawArray.size()];
        for (int i = 0; i < rawArray.size(); i++) {
            rawData[i] = (byte) rawArray.get(i).asInt();
        }
        return rawData;
    }

}
