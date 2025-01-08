package com.sungjin.airquailitymonitordemo.dto.request.HealthData;

import com.sungjin.airquailitymonitordemo.entity.enums.Provider;
import java.time.LocalDate;

import com.sungjin.airquailitymonitordemo.entity.HealthUser;
import com.sungjin.airquailitymonitordemo.entity.enums.BloodType;
import com.sungjin.airquailitymonitordemo.entity.enums.Gender;

public record HealthUserInfoRequestDto(
        String email,
        Long projectId,
        String provider,
        String birthDate,
        String bloodType,
        String biologicalSex
) {
    public HealthUser toEntity() {
        return HealthUser.builder()
                .email(email)
                .provider(Provider.toProvider(provider))
                .birthDate(LocalDate.parse(birthDate))
                .bloodType(BloodType.toBloodType(bloodType))
                .biologicalSex(Gender.toGender(biologicalSex))
                .build();
    }
}