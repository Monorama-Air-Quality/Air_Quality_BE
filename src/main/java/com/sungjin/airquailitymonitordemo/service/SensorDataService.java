package com.sungjin.airquailitymonitordemo.service;

import com.sungjin.airquailitymonitordemo.entity.SensorData;
import com.sungjin.airquailitymonitordemo.dto.request.SensorDataRequestDto;
import com.sungjin.airquailitymonitordemo.repository.SensorDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class SensorDataService {

    private final RedisTemplate<String, SensorData> redisTemplate;
    private final SensorDataRepository sensorDataRepository;
    private final KafkaTemplate<String, SensorData> kafkaTemplate;
    private final RedisTemplate<String, SensorData> sensorDataRedisTemplate;


    @Value("${kafka.topic.sensor-data}")
    private String topicName;

    // Redis의 데이터를 mysql에 저장
    @Scheduled(fixedRate = 5000)  // 5초마다 실행
    public void processRedisData() {
        String pattern = "device:*:latest";
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys != null) {
            for (String key : keys) {
                SensorData data = redisTemplate.opsForValue().get(key);
                if (data != null) {
                    try {
                        // MySQL에 저장
                        sensorDataRepository.save(data);

                        // Kafka로 전송
                        sendToKafka(data);

                        // TTL 갱신 (데이터 삭제하지 않음)
                        //redisTemplate.expire(key, 24, TimeUnit.HOURS);
                        redisTemplate.delete(key);

                        log.info("Processed data from Redis: deviceId={}", data.getDeviceId());
                    } catch (Exception e) {
                        log.error("Error processing data from Redis: {}", e.getMessage(), e);
                    }
                }
            }
        }
    }

    public SensorData processSensorData(SensorDataRequestDto requestDto) {
        SensorData sensorData = convertToEntity(requestDto);

        // MySQL 저장
        try {
            sensorData = sensorDataRepository.save(sensorData);
            log.info("Received sensor data: {}", sensorData);
        } catch (Exception e) {
            log.error("Error processing sensor data MySQL", e);
        }

        // Redis 캐시
        try {
            cacheLatestData(sensorData);
            log.info("Successfully processed and cached sensor data for device: {}", sensorData.getDeviceId());
        } catch (Exception e) {
            log.error("Error caching sensor data for device: {}", sensorData.getDeviceId(), e);
        }

        return sensorData;
    }
    private void cacheLatestData(SensorData data) {
        if (data.getDeviceId() == null) {
            log.warn("DeviceId is null, skipping Redis cache");
            return;
        }

        String redisKey = "device:" + data.getDeviceId() + ":latest";
        try {
            // 테스트 출력
            log.debug("Caching data - Key: {}, Value: {}", redisKey, data);

            // Redis에 저장 전에 명시적으로 키 삭제
            sensorDataRedisTemplate.delete(redisKey);

            // 데이터 저장 (만료시간 24시간)
            boolean result = Boolean.TRUE.equals(sensorDataRedisTemplate.opsForValue()
                    .setIfAbsent(redisKey, data, 24, TimeUnit.HOURS));

            // 저장 결과 확인
            if (result) {
                log.info("Successfully cached sensor data for device: {}", data.getDeviceId());
                // 저장된 데이터 확인
                SensorData cachedData = sensorDataRedisTemplate.opsForValue().get(redisKey);
                log.info("Cached data verification: {}", cachedData != null ? "success" : "failed");
            } else {
                log.warn("Failed to cache sensor data for device: {}", data.getDeviceId());
            }
        } catch (Exception e) {
            log.error("Error caching sensor data for device: {} - Error: {}",
                    data.getDeviceId(), e.getMessage(), e);
        }
    }

    private void sendToKafka(SensorData sensorData) {
        try {
            Message<SensorData> message = MessageBuilder
                    .withPayload(sensorData)
                    .setHeader(KafkaHeaders.TOPIC, topicName)
                    .setHeader(KafkaHeaders.KEY, sensorData.getDeviceId())
                    .build();

            kafkaTemplate.send(message)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Sent sensor data to Kafka: deviceId={}", sensorData.getDeviceId());
                        } else {
                            log.error("Failed to send sensor data to Kafka for deviceId={}",
                                    sensorData.getDeviceId(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Kafka send failed", e);
            // Kafka 실패는 무시하고 계속 진행
        }
    }

    private SensorData convertToEntity(SensorDataRequestDto dto) {
        return SensorData.builder()
                .deviceId(dto.deviceId())
                .timestamp(dto.timestamp())
                .pm25Value(dto.pm25Value())
                .pm25Level(dto.pm25Level())
                .pm10Value(dto.pm10Value())
                .pm10Level(dto.pm10Level())
                .temperature(dto.temperature())
                .temperatureLevel(dto.temperatureLevel())
                .humidity(dto.humidity())
                .humidityLevel(dto.humidityLevel())
                .co2Value(dto.co2Value())
                .co2Level(dto.co2Level())
                .vocValue(dto.vocValue())
                .vocLevel(dto.vocLevel())
                .latitude(dto.latitude())
                .longitude(dto.longitude())
                .rawData(dto.rawData())
                .build();
    }

    public Page<SensorData> getDeviceHistory(
            String deviceId,
            LocalDateTime start,
            LocalDateTime end,
            PageRequest pageRequest
    ) {
        return sensorDataRepository.findByDeviceIdAndTimestampBetween(
                deviceId, start, end, pageRequest);
    }

    public SensorData getLatestSensorData(String deviceId) {
        String redisKey = "device:" + deviceId + ":latest";
        SensorData data = redisTemplate.opsForValue().get(redisKey);

        if (data == null) {
            // Redis에 없으면 DB에서 조회
            data = sensorDataRepository.findFirstByDeviceIdOrderByTimestampDesc(deviceId)
                    .orElse(null);

            // 있으면 Redis에 캐시
            if (data != null) {
                cacheLatestData(data);
            }
        }

        return data;
    }
}