package com.sungjin.airquailitymonitordemo.service;

import com.sungjin.airquailitymonitordemo.dto.request.DeviceStatusRequestDto;
import com.sungjin.airquailitymonitordemo.entity.DeviceStatus;
import com.sungjin.airquailitymonitordemo.repository.DeviceStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceStatusService {

    private final RedisTemplate<String, DeviceStatus> deviceStatusRedisTemplate;

    private final DeviceStatusRepository deviceStatusRepository;

    private final KafkaTemplate<String, DeviceStatus> deviceStatusKafkaTemplate;

    @Value("${kafka.topic.device-status}")
    private String topicName;

    @Scheduled(fixedRate = 5000)
    public void processRedisData() {
        try {
            String pattern = "device:*:status";
            Set<String> keys = deviceStatusRedisTemplate.keys(pattern);
            
            if (keys != null) {
                for (String key : keys) {
                    DeviceStatus status = deviceStatusRedisTemplate.opsForValue().get(key);
                    if (status != null) {
                        try {
                            // MySQL에 저장
                            deviceStatusRepository.save(status);
                            
                            // Kafka로 전송
                            sendToKafka(status);

                            // Redis에서 처리된 데이터 삭제
                            deviceStatusRedisTemplate.delete(key);

                            log.info("Processed device status from Redis: deviceId={}", status.getDeviceId());
                        } catch (Exception e) {
                            log.error("Error processing device status from Redis: {}", e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error processing Redis device status data: {}", e.getMessage());
        }
    }

    public void updateDeviceStatus(String deviceId, DeviceStatusRequestDto request) {
        DeviceStatus status = convertToEntity(deviceId, request);
        try {
            // Redis에만 저장
            cacheDeviceStatus(status);
        } catch (Exception e) {
            log.error("Failed to cache device status in Redis", e);
            // Redis 실패시 바로 MySQL에 저장
            deviceStatusRepository.save(status);
        }
    }

    private void cacheDeviceStatus(DeviceStatus status) {
        String redisKey = "device:" + status.getDeviceId() + ":status";
        deviceStatusRedisTemplate.opsForValue().set(redisKey, status);
        deviceStatusRedisTemplate.expire(redisKey, 1, TimeUnit.HOURS);
    }

    private void sendToKafka(DeviceStatus status) {
        try {
            Message<DeviceStatus> message = MessageBuilder
                .withPayload(status)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .setHeader(KafkaHeaders.KEY, status.getDeviceId())
                .build();

            deviceStatusKafkaTemplate.send(message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Sent device status to Kafka: deviceId={}", status.getDeviceId());
                    } else {
                        log.error("Failed to send device status to Kafka for deviceId={}", 
                            status.getDeviceId(), ex);
                    }
                });
        } catch (Exception e) {
            log.error("Kafka send failed for device status", e);
        }
    }

    private DeviceStatus convertToEntity(String deviceId, DeviceStatusRequestDto dto) {
        return DeviceStatus.builder()
                .deviceId(deviceId)
                .timestamp(LocalDateTime.now())
                .connectionStatus(dto.connectionStatus())          // Enum 타입
                .firmwareVersion(dto.firmwareVersion())
                .batteryStatus(dto.batteryStatus())               // Enum 타입
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
        DeviceStatus status = deviceStatusRedisTemplate.opsForValue().get(redisKey);

        if (status == null) {
            status = deviceStatusRepository.findByDeviceId(deviceId)
                    .orElse(null);

            if (status != null) {
                deviceStatusRedisTemplate.opsForValue().set(redisKey, status);
                deviceStatusRedisTemplate.expire(redisKey, 1, TimeUnit.HOURS);
            }
        }

        return status;
    }
}
