package com.sungjin.airquailitymonitordemo.dto;

public record DeviceLocationDto(
        Integer floorLevel,
        String placeType,
        String description
) {}