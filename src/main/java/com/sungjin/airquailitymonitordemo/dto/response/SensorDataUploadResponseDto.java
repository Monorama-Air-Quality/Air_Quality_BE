package com.sungjin.airquailitymonitordemo.dto.response;

public record SensorDataUploadResponseDto(
        boolean success,
        String message,
        int processedCount
) {}
