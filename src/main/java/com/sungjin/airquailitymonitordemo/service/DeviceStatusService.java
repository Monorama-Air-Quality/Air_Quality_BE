package com.sungjin.airquailitymonitordemo.service;

import com.sungjin.airquailitymonitordemo.dto.request.DeviceStatusRequestDto;
import com.sungjin.airquailitymonitordemo.entity.DeviceStatus;
import com.sungjin.airquailitymonitordemo.repository.DeviceStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceStatusService {

    private RedisTemplate<String, DeviceStatus> redisTemplate;

    private DeviceStatusRepository deviceStatusRepository;

    public void updateDeviceStatus(String deviceId, DeviceStatusRequestDto request) {
        DeviceStatus status = convertToEntity(deviceId, request);
        deviceStatusRepository.save(status);

        String redisKey = "device:" + deviceId + ":status";
        redisTemplate.opsForValue().set(redisKey, status);
        redisTemplate.expire(redisKey, 1, TimeUnit.HOURS);
    }

    private DeviceStatus convertToEntity(String deviceId, DeviceStatusRequestDto dto) {
        return DeviceStatus.builder()
                .deviceId(deviceId)
                .timestamp(LocalDateTime.now())
                .connectionStatus(dto.connectionStatus())
                .firmwareVersion(dto.firmwareVersion())
                .batteryStatus(dto.batteryStatus())
                .batteryLevel(dto.batteryLevel())
                .lastErrorMessage(dto.lastErrorMessage())
                .lastConnectedAt(dto.lastConnectedAt())
                .lastDisconnectedAt(dto.lastDisconnectedAt())
                .ipAddress(dto.ipAddress())
                .rssi(dto.rssi())
                .build();
    }

    public DeviceStatus getDeviceStatus(String deviceId) {
        String redisKey = "device:" + deviceId + ":status";
        DeviceStatus status = redisTemplate.opsForValue().get(redisKey);

        if (status == null) {
            status = deviceStatusRepository.findByDeviceId(deviceId)
                    .orElse(null);

            if (status != null) {
                redisTemplate.opsForValue().set(redisKey, status);
                redisTemplate.expire(redisKey, 1, TimeUnit.HOURS);
            }
        }

        return status;
    }
}
