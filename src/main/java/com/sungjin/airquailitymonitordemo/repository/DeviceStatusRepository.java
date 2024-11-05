package com.sungjin.airquailitymonitordemo.repository;

import com.sungjin.airquailitymonitordemo.entity.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceStatusRepository extends JpaRepository<DeviceStatus, Long> {
    Optional<DeviceStatus> findByDeviceId(String deviceId);
}