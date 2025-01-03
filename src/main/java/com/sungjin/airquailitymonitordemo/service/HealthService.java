package com.sungjin.airquailitymonitordemo.service;

import com.sungjin.airquailitymonitordemo.dto.request.HealthData.HealthDataRequestDto;
import com.sungjin.airquailitymonitordemo.entity.HealthUser;
import com.sungjin.airquailitymonitordemo.repository.HealthDataRepository;

import com.sungjin.airquailitymonitordemo.repository.HealthUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class HealthService {
    private final HealthDataRepository healthDataRepository;
    private final HealthUserRepository healthUserRepository;

    // HealthData 엔티티에 데이터를 저장하는 함수
    public void saveHealthData(HealthDataRequestDto healthData) {
        HealthUser user = healthUserRepository.findByEmail(healthData.userInfo().email())
                .orElseGet(() -> healthUserRepository.save(healthData.userInfo().toEntity()));
        healthDataRepository.save(healthData.measurements().toEntity(user));
    }
}