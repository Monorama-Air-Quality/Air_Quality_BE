package com.sungjin.airquailitymonitordemo.dto.request;

import com.sungjin.airquailitymonitordemo.dto.TransmissionMode;

public record DeviceRegistrationRequestDto(
        String deviceId,
        Long projectId,
        String userName,
        String userEmail,
        TransmissionMode transmissionMode,
        Integer uploadInterval
) {}
