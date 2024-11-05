package com.sungjin.airquailitymonitordemo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String deviceId;
    
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime timestamp;
    
    private Double pm25Value;
    private Integer pm25Level;
    private Double pm10Value;
    private Integer pm10Level;
    private Double temperature;
    private Integer temperatureLevel;
    private Double humidity;
    private Integer humidityLevel;
    private Double co2Value;
    private Integer co2Level;
    private Double vocValue;
    private Integer vocLevel;
    private Double latitude;
    private Double longitude;
    
    @Lob
    private byte[] rawData;
} 