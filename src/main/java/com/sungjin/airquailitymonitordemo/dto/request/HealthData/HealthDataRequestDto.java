package com.sungjin.airquailitymonitordemo.dto.request.HealthData;

public record HealthDataRequestDto(
    HealthUserInfoRequestDto userInfo,
    MeasurementsRequestDto measurements
) {}