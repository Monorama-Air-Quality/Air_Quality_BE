package com.sungjin.airquailitymonitordemo.repository;

import com.sungjin.airquailitymonitordemo.entity.SensorData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    Page<SensorData> findByDeviceIdAndTimestampBetween(
            String deviceId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Optional<SensorData> findFirstByDeviceIdOrderByTimestampDesc(String deviceId);
}