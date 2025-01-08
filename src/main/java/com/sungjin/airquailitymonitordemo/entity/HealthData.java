package com.sungjin.airquailitymonitordemo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "health_data")
public class HealthData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Double stepCount;

    @Column
    private Double runningSpeed;

    @Column
    private Double basalEnergyBurned;

    @Column
    private Double activeEnergyBurned;

    @Column
    private String sleepAnalysis; 

    @Column
    private Double height;

    @Column
    private Double bodyMass;

    @Column
    private Double heartRate;

    @Column
    private Double oxygenSaturation;

    @Column
    private Double bloodPressureSystolic;

    @Column
    private Double bloodPressureDiastolic;

    @Column
    private Double respiratoryRate;

    @Column
    private Double bodyTemperature;

    @Column
    private LocalDateTime createdAt; // 데이터를 저장한 시점

    @Column
    private Double latitude; // 위도

    @Column
    private Double longitude; // 경도

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_user_id")
    private HealthUser healthUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
}