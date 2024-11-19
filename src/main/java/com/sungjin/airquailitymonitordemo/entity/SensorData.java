package com.sungjin.airquailitymonitordemo.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sungjin.airquailitymonitordemo.utils.ByteArrayJsonDeserializer;
import com.sungjin.airquailitymonitordemo.utils.ByteArrayJsonSerializer;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SensorData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(columnDefinition = "TIMESTAMP")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
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
    @JsonSerialize(using = ByteArrayJsonSerializer.class)
    @JsonDeserialize(using = ByteArrayJsonDeserializer.class)
    private byte[] rawData;
}