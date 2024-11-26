package com.sungjin.airquailitymonitordemo.dto.request;

public record DeviceLocationRequestDto(
        Integer floorLevel,
        String placeType,
        String description
) {}