package com.sungjin.airquailitymonitordemo.repository;

import com.sungjin.airquailitymonitordemo.entity.SensorData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

    Optional<SensorData> findFirstByDeviceIdOrderByTimestampDesc(String deviceId);

    @Query("SELECT s FROM SensorData s WHERE " +
            "(:latStart IS NULL OR (s.latitude >= :latStart AND s.latitude <= :latEnd)) AND " +
            "(:longStart IS NULL OR (s.longitude >= :longStart AND s.longitude <= :longEnd)) AND " +
            "(:startDate IS NULL OR (:startDate IS NOT NULL AND :endDate IS NOT NULL AND s.timestamp BETWEEN :startDate AND :endDate))")
    Page<SensorData> findBySearchCriteria(
            @Param("latStart") Double latitudeStart,
            @Param("latEnd") Double latitudeEnd,
            @Param("longStart") Double longitudeStart,
            @Param("longEnd") Double longitudeEnd,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

}