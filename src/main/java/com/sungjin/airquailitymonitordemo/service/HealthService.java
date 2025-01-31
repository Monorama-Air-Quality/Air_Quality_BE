package com.sungjin.airquailitymonitordemo.service;

import com.sungjin.airquailitymonitordemo.dto.request.HealthData.HealthUserInfoRequestDto;
import com.sungjin.airquailitymonitordemo.dto.request.HealthData.MeasurementsRequestDto;
import com.sungjin.airquailitymonitordemo.entity.HealthData;
import com.sungjin.airquailitymonitordemo.entity.HealthUser;
import com.sungjin.airquailitymonitordemo.entity.Project;
import com.sungjin.airquailitymonitordemo.entity.UserProject;
import com.sungjin.airquailitymonitordemo.repository.HealthDataRepository;
import com.sungjin.airquailitymonitordemo.repository.HealthUserRepository;
import com.sungjin.airquailitymonitordemo.repository.ProjectRepository;
import com.sungjin.airquailitymonitordemo.repository.UserProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class HealthService {
    private final HealthDataRepository healthDataRepository;
    private final HealthUserRepository healthUserRepository;
    private final ProjectRepository projectRepository;
    private final UserProjectRepository userProjectRepository;

    public void saveHealthData(HealthUserInfoRequestDto userInfo, List<MeasurementsRequestDto> measurements) {
        // 1. 프로젝트 조회
        Project project = projectRepository.findById(userInfo.projectId())
            .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        // 2. 유저 조회 또는 생성
        HealthUser user = healthUserRepository.findByEmail(userInfo.email())
                .orElseGet(() -> healthUserRepository.save(userInfo.toEntity()));

        // 3. UserProject 관계 생성 (없는 경우에만)
        if (!userProjectRepository.existsByHealthUserAndProject(user, project)) {
            UserProject userProject = UserProject.builder()
                .healthUser(user)
                .project(project)
                .build();
            userProjectRepository.save(userProject);
        }

        // 4. 헬스 데이터 저장 (단일 또는 배치)
        List<HealthData> healthDataList = measurements.stream()
            .map(measurement -> measurement.toEntity(user, project))
            .collect(Collectors.toList());

        healthDataRepository.saveAll(healthDataList);
        log.info("Saved {} health data records for user {}", healthDataList.size(), user.getEmail());
    }
}