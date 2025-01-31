package com.sungjin.airquailitymonitordemo.dto.request.HealthData;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sungjin.airquailitymonitordemo.entity.HealthData;
import com.sungjin.airquailitymonitordemo.entity.HealthUser;
import com.sungjin.airquailitymonitordemo.entity.Project;

public record MeasurementsRequestDto(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime timestamp,
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
    public HealthData toEntity(HealthUser healthUser, Project project) {
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
                .project(project)
                .createdAt(timestamp)
                .build();
    }
}