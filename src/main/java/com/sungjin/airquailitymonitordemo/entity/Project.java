package com.sungjin.airquailitymonitordemo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "project")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    private String projectName;

    @Column
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<SensorData> sensorDataList;

    @OneToMany(mappedBy = "project")
    private List<Device> devices;

    @OneToMany(mappedBy = "project")
    private List<UserProject> userProjects;

    @OneToMany(mappedBy = "project")
    private List<HealthData> healthDataList;

}