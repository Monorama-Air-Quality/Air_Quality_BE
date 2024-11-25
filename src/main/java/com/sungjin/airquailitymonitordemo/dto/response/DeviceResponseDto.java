package com.sungjin.airquailitymonitordemo.dto.response;

public record DeviceResponseDto(
        String deviceId,
        String userName,
        String userEmail,
        Long projectId
) {}