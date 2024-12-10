package com.sungjin.airquailitymonitordemo.repository;

import com.sungjin.airquailitymonitordemo.entity.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthDataRepository extends JpaRepository<HealthData, Long> {

}
