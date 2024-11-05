package com.sungjin.airquailitymonitordemo.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "device_status")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceStatus implements Serializable {
    private static final long serialVersionUID = 1L;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public enum DeviceConnectionStatus {
        CONNECTED,
        DISCONNECTED,
        CONNECTING,
        ERROR
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public enum BatteryStatus {
        FULL,
        NORMAL,
        LOW,
        CRITICAL
    }
}