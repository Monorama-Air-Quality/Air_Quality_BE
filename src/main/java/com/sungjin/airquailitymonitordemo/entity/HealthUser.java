package com.sungjin.airquailitymonitordemo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import com.sungjin.airquailitymonitordemo.entity.enums.BloodType;
import com.sungjin.airquailitymonitordemo.entity.enums.Gender;
import com.sungjin.airquailitymonitordemo.entity.enums.Provider;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "health_user")
public class HealthUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    @Column
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column
    private LocalDate birthDate;

    @Column
    @Enumerated(EnumType.STRING)
    private BloodType bloodType;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender biologicalSex;

    @OneToMany(mappedBy = "healthUser")
    private List<HealthData> healthDataList;

    @OneToMany(mappedBy = "healthUser")
    private List<UserProject> userProjects;
}