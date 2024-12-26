package com.sungjin.airquailitymonitordemo.dto.request.HealthData;

import java.time.LocalDateTime;

import com.sungjin.airquailitymonitordemo.entity.HealthData;
import com.sungjin.airquailitymonitordemo.entity.HealthUser;

public record MeasurementsRequestDto(
    Double stepCount,
    Double heartRate,
    Double bloodPressureSystolic,
    Double bloodPressureDiastolic,
    Double oxygenSaturation,
    Double bodyTemperature,
    Double respiratoryRate,
    Double height,
    Double weight,
    Double runningSpeed,
    Double activeEnergy,
    Double basalEnergy,
    Double latitude,
    Double longitude
) {
    public HealthData toEntity(HealthUser healthUser) {
        return HealthData.builder()
                .stepCount(stepCount)
                .heartRate(heartRate)
                .bloodPressureSystolic(bloodPressureSystolic)
                .bloodPressureDiastolic(bloodPressureDiastolic)
                .oxygenSaturation(oxygenSaturation)
                .bodyTemperature(bodyTemperature)
                .respiratoryRate(respiratoryRate)
                .height(height)
                .bodyMass(weight)
                .runningSpeed(runningSpeed)
                .activeEnergyBurned(activeEnergy)
                .basalEnergyBurned(basalEnergy)
                .latitude(latitude)
                .longitude(longitude)
                .healthUser(healthUser)
                .createdAt(LocalDateTime.now())
                .build();
    }
}