package com.sungjin.airquailitymonitordemo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "device_status")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String deviceId;
    
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime timestamp;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DeviceConnectionStatus connectionStatus;
    
    private String firmwareVersion;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BatteryStatus batteryStatus;
    
    private Double batteryLevel;
    private String lastErrorMessage;
    private LocalDateTime lastConnectedAt;
    private LocalDateTime lastDisconnectedAt;
    private String ipAddress;
    private Integer rssi;

    public enum DeviceConnectionStatus {
        CONNECTED,
        DISCONNECTED,
        CONNECTING,
        ERROR
    }

    public enum BatteryStatus {
        FULL,
        NORMAL,
        LOW,
        CRITICAL
    }
} 