package com.sungjin.airquailitymonitordemo.repository;

import com.sungjin.airquailitymonitordemo.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
} 