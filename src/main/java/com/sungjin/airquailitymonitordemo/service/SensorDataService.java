package com.sungjin.airquailitymonitordemo.service;

import com.sungjin.airquailitymonitordemo.entity.SensorData;
import com.sungjin.airquailitymonitordemo.dto.request.SensorDataRequestDto;
import com.sungjin.airquailitymonitordemo.repository.SensorDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SensorDataService {

    @Autowired
    private RedisTemplate<String, SensorData> redisTemplate;

    @Autowired
    private SensorDataRepository sensorDataRepository;

    public void processSensorData(SensorDataRequestDto requestDto) {
        SensorData sensorData = convertToEntity(requestDto);
        saveSensorData(requestDto);
        cacheLatestData(sensorData);
    }

    public void saveSensorData(SensorDataRequestDto requestDto) {
        SensorData sensorData = convertToEntity(requestDto);
        sensorDataRepository.save(sensorData);
    }

    private void cacheLatestData(SensorData data) {
        String redisKey = "device:" + data.getDeviceId() + ":latest";
        redisTemplate.opsForValue().set(redisKey, data);
        redisTemplate.expire(redisKey, 1, TimeUnit.HOURS);
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