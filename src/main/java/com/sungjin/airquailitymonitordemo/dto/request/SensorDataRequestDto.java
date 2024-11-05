package com.sungjin.airquailitymonitordemo.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record SensorDataRequestDto(
    String deviceId,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime timestamp,
    Double pm25Value,
    Integer pm25Level,
    Double pm10Value,
    Integer pm10Level,
    Double temperature,
    Integer temperatureLevel,
    Double humidity,
    Integer humidityLevel,
    Double co2Value,
    Integer co2Level,
    Double vocValue,
    Integer vocLevel,
    Double latitude,
    Double longitude,
    byte[] rawData
) {} 