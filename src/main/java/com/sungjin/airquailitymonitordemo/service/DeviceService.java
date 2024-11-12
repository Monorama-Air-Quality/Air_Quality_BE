package com.sungjin.airquailitymonitordemo.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceService {
    private final EntityManager entityManager;

    @Transactional
    public void upsertDevice(String deviceId, String placeType, Integer floorLevel, String description) {
        try {
            StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("upsert_device_info")
                .registerStoredProcedureParameter("p_device_id", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_place_type", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_floor_level", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_description", String.class, ParameterMode.IN)
                .setParameter("p_device_id", deviceId)
                .setParameter("p_place_type", placeType)
                .setParameter("p_floor_level", floorLevel)
                .setParameter("p_description", description);

            query.execute();
            log.info("Successfully saved device info - deviceId: {}, placeType: {}, floorLevel: {} description: {}",
                    deviceId, placeType, floorLevel, description);
        } catch (Exception e) {
            log.error("Error saving device info: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save device info", e);
        }
    }
} 