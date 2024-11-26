package com.sungjin.airquailitymonitordemo.dto.request;

public record DeviceRegistrationRequestDto(
        String deviceId,
        Long projectId,
        String userName,
        String userEmail
) {
}
