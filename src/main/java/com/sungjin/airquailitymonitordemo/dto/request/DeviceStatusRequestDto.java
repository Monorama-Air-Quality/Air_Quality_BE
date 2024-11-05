package com.sungjin.airquailitymonitordemo.dto.request;

import com.sungjin.airquailitymonitordemo.entity.DeviceStatus;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public record DeviceStatusRequestDto(
    String deviceId,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime timestamp,
    DeviceStatus.DeviceConnectionStatus connectionStatus,
    String firmwareVersion,
    DeviceStatus.BatteryStatus batteryStatus,
    Double batteryLevel,
    String lastErrorMessage,
    LocalDateTime lastConnectedAt,
    LocalDateTime lastDisconnectedAt,
    String ipAddress,
    Integer rssi
) {} 