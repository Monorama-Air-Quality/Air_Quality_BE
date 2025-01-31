package com.sungjin.airquailitymonitordemo.dto.request.HealthData;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public record HealthDataRequestDto(
    @JsonProperty("userInfo")
    HealthUserInfoRequestDto userInfo,
    
    @JsonProperty("measurements")
    List<MeasurementsRequestDto> measurements
) {}