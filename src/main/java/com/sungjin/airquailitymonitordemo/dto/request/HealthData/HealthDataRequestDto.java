package com.sungjin.airquailitymonitordemo.dto.request.HealthData;

import java.time.LocalDateTime;

public record HealthDataRequestDto(
    String email,
    String provider,
    HealthUserInfoRequestDto userInfo,
    MeasurementsRequestDto measurements,
    LocalDateTime timestamp
) {}