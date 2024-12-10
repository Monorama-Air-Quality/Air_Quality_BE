package com.sungjin.airquailitymonitordemo.dto.request.HealthData;

import java.time.LocalDate;

import com.sungjin.airquailitymonitordemo.entity.HealthUser;
import com.sungjin.airquailitymonitordemo.entity.enums.BloodType;
import com.sungjin.airquailitymonitordemo.entity.enums.Gender;

public record HealthUserInfoRequestDto(
    String bloodType,
    String biologicalSex,
    String birthDate
) {
    public HealthUser toEntity() {
        return HealthUser.builder()
                .bloodType(BloodType.toBloodType(bloodType))
                .biologicalSex(Gender.toGender(biologicalSex))
                .birthDate(LocalDate.parse(birthDate))
                .build();
    }
}