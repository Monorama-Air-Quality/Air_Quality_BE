package com.sungjin.airquailitymonitordemo.dto;

import java.time.LocalDateTime;

public record DeviceLocationDto(
        Integer floorLevel,
        String placeType,
        String description

) {}