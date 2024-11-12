package com.sungjin.airquailitymonitordemo.dto.request;

public record DeviceInfoRequestDto(
    String deviceId,
    String placeType,
    Integer floorLevel,
    String description
) {} 