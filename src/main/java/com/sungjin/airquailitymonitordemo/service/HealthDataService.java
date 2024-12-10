package com.sungjin.airquailitymonitordemo.service;

import com.sungjin.airquailitymonitordemo.dto.request.HealthData.HealthDataRequestDto;
import com.sungjin.airquailitymonitordemo.repository.HealthDataRepository;
import com.sungjin.airquailitymonitordemo.repository.HealthUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class HealthDataService {
    private final HealthDataRepository healthDataRepository;
    private final HealthUserRepository healthUserRepository;
    
    // HealthData 엔티티에 데이터를 저장하는 함수
    public void saveHealthData(HealthDataRequestDto healthData) {
        if (!healthUserRepository.existsHealthUserByEmail(healthData.email())) {
            healthUserRepository.save(healthData.userInfo().toEntity());
        }

        healthDataRepository.save(healthData.measurements().toEntity());
    }
}