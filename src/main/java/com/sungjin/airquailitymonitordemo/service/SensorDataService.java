package com.sungjin.airquailitymonitordemo.service;

import com.sungjin.airquailitymonitordemo.dto.request.SensorDataSearchRequestDto;
import com.sungjin.airquailitymonitordemo.dto.response.SensorDataResponseDto;
import com.sungjin.airquailitymonitordemo.entity.Project;
import com.sungjin.airquailitymonitordemo.entity.SensorData;
import com.sungjin.airquailitymonitordemo.dto.request.SensorDataRequestDto;
import com.sungjin.airquailitymonitordemo.exception.InvalidSearchCriteriaException;
import com.sungjin.airquailitymonitordemo.repository.ProjectRepository;
import com.sungjin.airquailitymonitordemo.repository.SensorDataRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
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
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class SensorDataService {

    private final RedisTemplate<String, SensorData> sensorDataRedisTemplate;
    private final SensorDataRepository sensorDataRepository;
    private final KafkaTemplate<String, SensorData> kafkaTemplate;
    private final ProjectRepository projectRepository;


    @Value("${kafka.topic.sensor-data}")
    private String topicName;

    // Redis의 데이터를 mysql에 저장
    @Scheduled(fixedRate = 5000)
    public void processRedisData() {
        try {
            log.info("Starting scheduled process at: {}", LocalDateTime.now());

            // 1. 일반 데이터 키 패턴으로 변경
            Set<String> keys = sensorDataRedisTemplate.keys("device:*:data:*");
            log.info("Found {} Redis keys", keys != null ? keys.size() : 0);

            if (keys != null && !keys.isEmpty()) {
                int processedCount = 0;

                for (String key : keys) {
                    SensorData data = sensorDataRedisTemplate.opsForValue().get(key);
                    if (data != null) {
                        // MySQL 저장
                        try {
                            sensorDataRepository.save(data);
                            processedCount++;
                            log.info("Saved to MySQL - Device: {}, Time: {}",
                                    data.getDeviceId(), data.getTimestamp());
                        } catch (Exception e) {
                            log.error("MySQL save failed - Device: {}, Error: {}",
                                    data.getDeviceId(), e.getMessage());
                            continue;
                        }

                        // Kafka 전송
                        try {
                            sendToKafka(data);
                        } catch (Exception e) {
                            log.warn("Kafka send failed - Device: {}", data.getDeviceId());

                            continue;
                        }

                        // Redis에서 삭제
                        sensorDataRedisTemplate.delete(key);
                    }
                }

                log.info("Processed {} records at {}", processedCount, LocalDateTime.now());
            }

        } catch (Exception e) {
            log.error("Error in scheduled process: {}", e.getMessage(), e);
        }
    }

    /**
     * 센서 데이터 처리 (cache redis)
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

        try {
            // 1. 모든 데이터용 키 (타임스탬프 포함)
            String dataKey = String.format("device:%s:data:%s",
                    deviceId, data.getTimestamp().toString());

            // 2. 최신 데이터용 키
            String latestKey = String.format("device:%s:latest", deviceId);

            // 일반 데이터는 5초 동안만 보관
            sensorDataRedisTemplate.opsForValue()
                    .set(dataKey, data, 5, TimeUnit.SECONDS);

            // latest 데이터는 24시간 보관
            sensorDataRedisTemplate.opsForValue()
                    .set(latestKey, data, 24, TimeUnit.HOURS);

            log.info("Cached sensor data - Device: {}, Time: {}",
                    deviceId, data.getTimestamp());

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
                        log.info("Sent to Kafka: {}", sensorData.getDeviceId());
                    } else {
                        log.error("Kafka send failed: {}", ex.getMessage());
                    }
                });
        } catch (Exception e) {
            log.error("Error sending to Kafka", e);
            throw e;
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

    public SensorData getLatestSensorData(String deviceId) {
        String redisKey = "device:" + deviceId + ":latest";
        SensorData data = sensorDataRedisTemplate.opsForValue().get(redisKey);

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

    public Page<SensorDataResponseDto> searchSensorData(
            SensorDataSearchRequestDto searchRequest,
            PageRequest pageRequest
    ) {
        validateSearchCriteria(searchRequest);

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        if (searchRequest.dateRange().exists()) {
            startDateTime = searchRequest.dateRange().startDate().atStartOfDay();
            endDateTime = searchRequest.dateRange().endDate().atTime(23, 59, 59);
        }

        Double latStart = null;
        Double latEnd = null;
        Double longStart = null;
        Double longEnd = null;

        if (searchRequest.location().exists()) {
            latStart = searchRequest.location().getLatitudeStart();
            latEnd = searchRequest.location().getLatitudeEnd();
            longStart = searchRequest.location().getLongitudeStart();
            longEnd = searchRequest.location().getLongitudeEnd();
        }

        Page<SensorData> sensorDataPage = sensorDataRepository.findBySearchCriteria(
                latStart,
                latEnd,
                longStart,
                longEnd,
                startDateTime,
                endDateTime,
                pageRequest
        );

        return sensorDataPage.map(SensorDataResponseDto::fromEntity);
    }

    private void validateSearchCriteria(SensorDataSearchRequestDto searchRequest) {
        if (!searchRequest.hasValidCriteria()) {
            throw new InvalidSearchCriteriaException("Invalid search criteria provided");
        }

        if (!searchRequest.location().isValid()) {
            throw new InvalidSearchCriteriaException("Both latitude and longitude must be provided together");
        }

        if (!searchRequest.dateRange().isValid()) {
            throw new InvalidSearchCriteriaException("Both start date and end date must be provided together");
        }

        if (!searchRequest.dateRange().isValidRange()) {
            throw new InvalidSearchCriteriaException("Start date must be before end date");
        }

        if (!searchRequest.location().exists() && !searchRequest.dateRange().exists()) {
            throw new InvalidSearchCriteriaException("At least one search criteria (location or date) must be provided");
        }
    }



    public int processBatchData(List<SensorDataRequestDto> dataList) {
        List<SensorData> sensorDataList = new ArrayList<>();

        for (SensorDataRequestDto dto : dataList) {
            try {
                Project project = projectRepository.findById(dto.projectId())
                        .orElseThrow(() -> new EntityNotFoundException("Project not found: " + dto.projectId()));

                SensorData sensorData = SensorData.builder()
                        .deviceId(dto.deviceId())
                        .project(project)
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

                sensorDataList.add(sensorData);
            } catch (Exception e) {
                log.error("Error processing sensor data entry: {}", dto, e);
            }
        }

        if (!sensorDataList.isEmpty()) {
            sensorDataRepository.saveAll(sensorDataList);
        }

        return sensorDataList.size();
    }

    public Page<SensorDataResponseDto> getDeviceSensorHistory(
            String deviceId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            PageRequest pageRequest) {

        Specification<SensorData> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deviceId"), deviceId));

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("timestamp"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("timestamp"), endDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return sensorDataRepository.findAll(spec, pageRequest)
                .map(SensorDataResponseDto::fromEntity);
    }
}