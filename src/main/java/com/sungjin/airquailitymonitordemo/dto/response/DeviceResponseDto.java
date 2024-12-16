package com.sungjin.airquailitymonitordemo.dto.response;

import com.sungjin.airquailitymonitordemo.dto.DeviceLocationDto;
import com.sungjin.airquailitymonitordemo.dto.TransmissionMode;

import java.time.LocalDateTime;

public record DeviceResponseDto(
        String deviceId,
        String userName,
        String userEmail,
        Long projectId,
        DeviceLocationDto location,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        TransmissionMode transmissionMode,
        Integer uploadInterval
) {}