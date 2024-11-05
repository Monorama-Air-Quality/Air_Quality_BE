package com.sungjin.airquailitymonitordemo.controller;

import com.sungjin.airquailitymonitordemo.dto.request.SensorDataRequestDto;
import com.sungjin.airquailitymonitordemo.dto.request.DeviceStatusRequestDto;
import com.sungjin.airquailitymonitordemo.dto.response.ApiResponseDto;
import com.sungjin.airquailitymonitordemo.entity.DeviceStatus;
import com.sungjin.airquailitymonitordemo.entity.SensorData;
import com.sungjin.airquailitymonitordemo.service.DeviceStatusService;
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

@RestController
@RequestMapping("/api/v1/devices")
@Slf4j
@RequiredArgsConstructor
public class DeviceController {

    private final SensorDataService sensorDataService;
    private final DeviceStatusService deviceStatusService;

    @PostMapping("/data")
    public ResponseEntity<ApiResponseDto> postData(@RequestBody SensorDataRequestDto request) {
        try {
            log.info("Received sensor data: {}", request);
            sensorDataService.processSensorData(request);
            return ResponseEntity.ok(new ApiResponseDto("success", "Data processed successfully"));
        } catch (Exception e) {
            log.error("Error processing sensor data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto("error", "Error processing data: " + e.getMessage()));
        }
    }

    @GetMapping("/{deviceId}/history")
    public ResponseEntity<Page<SensorData>> getDeviceHistory(
            @PathVariable String deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp,desc") String[] sort
    ) {
        try {
            Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ?
                    Sort.Direction.DESC : Sort.Direction.ASC;

            PageRequest pageRequest = PageRequest.of(page, size,
                    Sort.by(direction, sort[0]));

            Page<SensorData> history = sensorDataService.getDeviceHistory(
                    deviceId, start, end, pageRequest);

            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Error fetching device history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{deviceId}/latest")
    public ResponseEntity<SensorData> getLatestData(
            @PathVariable String deviceId
    ) {
        try {
            SensorData latestData = sensorDataService.getLatestSensorData(deviceId);
            if (latestData == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(latestData);
        } catch (Exception e) {
            log.error("Error fetching latest data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{deviceId}/status")
    public ResponseEntity<ApiResponseDto> updateDeviceStatus(
            @PathVariable String deviceId,
            @RequestBody DeviceStatusRequestDto request
    ) {
        try {
            deviceStatusService.updateDeviceStatus(deviceId, request);
            return ResponseEntity.ok(new ApiResponseDto("success", "Status updated successfully"));
        } catch (Exception e) {
            log.error("Error updating device status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto("error", "Error updating status: " + e.getMessage()));
        }
    }

    @GetMapping("/{deviceId}/status")
    public ResponseEntity<DeviceStatus> getDeviceStatus(
            @PathVariable String deviceId
    ) {
        try {
            DeviceStatus status = deviceStatusService.getDeviceStatus(deviceId);
            if (status == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Error fetching device status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
