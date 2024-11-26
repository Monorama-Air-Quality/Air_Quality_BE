package com.sungjin.airquailitymonitordemo.dto.response;

import com.sungjin.airquailitymonitordemo.dto.DeviceLocationDto;

public record DeviceResponseDto(
        String deviceId,
        String userName,
        String userEmail,
        Long projectId,
        DeviceLocationDto location
) {}