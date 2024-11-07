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
    @Scheduled(fixedRate = 5000)
    public void processRedisData() {
        // latest가 아닌 모든 센서 데이터를 가져옴
        String pattern = "device:*:[0-9]*";  // timestamp 패턴
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys != null) {
            for (String key : keys) {
                SensorData data = redisTemplate.opsForValue().get(key);
                if (data != null) {
                    try {
                        sensorDataRepository.save(data);
                        sendToKafka(data);
                        redisTemplate.delete(key);

                        log.info("Processed data from Redis: deviceId={}, timestamp={}",
                                data.getDeviceId(), data.getTimestamp());
                    } catch (Exception e) {
                        log.error("Error processing data from Redis: {}", e.getMessage(), e);
                    }
                }
            }
        }
    }

    /**
     * 센서 데이터 처리
     * @param requestDto 센서 데이터 요청 DTO
     * @return 센서 데이터
     */
    public SensorData processSensorData(SensorDataRequestDto requestDto) {
        SensorData sensorData = convertToEntity(requestDto);

        // Redis에만 캐시
        try {
            cacheLatestData(sensorData);
            log.info("Successfully cached sensor data for device: {}", sensorData.getDeviceId());
        } catch (Exception e) {
            log.error("Error caching sensor data for device: {}", sensorData.getDeviceId(), e);
        }

        return sensorData;
    }

    /**
     * Redis에 최신 데이터 캐시 (저장)
     * @param data 센서 데이터
     */
    private void cacheLatestData(SensorData data) {
        String deviceId = data.getDeviceId();
        if (deviceId == null) {
            log.warn("DeviceId is null, skipping Redis cache");
            return;
        }

        // 타임스탬프를 키에 포함시켜 모든 데이터 보존
        String redisKey = String.format("device:%s:%s", deviceId,
                data.getTimestamp().toString());

        try {
            // Redis에 저장 (만료시간 24시간)
            boolean result = Boolean.TRUE.equals(sensorDataRedisTemplate.opsForValue()
                    .setIfAbsent(redisKey, data, 24, TimeUnit.HOURS));

            // latest 키도 함께 업데이트
            String latestKey = String.format("device:%s:latest", deviceId);
            sensorDataRedisTemplate.opsForValue()
                    .set(latestKey, data, 24, TimeUnit.HOURS);

            if (result) {
                log.info("Successfully cached sensor data for device: {}", deviceId);
            }
        } catch (Exception e) {
            log.error("Error caching sensor data for device: {}", deviceId, e);
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