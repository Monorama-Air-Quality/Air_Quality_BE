package com.sungjin.airquailitymonitordemo.dto.response;

public record SensorDataBatchResponseDto(
        boolean success,
        String message,
        int processedCount
) {}
