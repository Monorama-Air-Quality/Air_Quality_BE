package com.sungjin.airquailitymonitordemo.service;

import com.sungjin.airquailitymonitordemo.dto.request.HealthData.HealthDataRequestDto;
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

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class HealthService {
    private final HealthDataRepository healthDataRepository;
    private final HealthUserRepository healthUserRepository;
    private final ProjectRepository projectRepository;
    private final UserProjectRepository userProjectRepository;

    // HealthData 엔티티에 데이터를 저장하는 함수
    public void saveHealthData(HealthDataRequestDto healthData) {
        // 1. 프로젝트 조회
        Project project = projectRepository.findById(healthData.userInfo().projectId())
            .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        // 2. 유저 조회 또는 생성
        HealthUser user = healthUserRepository.findByEmail(healthData.userInfo().email())
                .orElseGet(() -> healthUserRepository.save(healthData.userInfo().toEntity()));

        // 3. UserProject 관계 생성 (없는 경우에만)
        if (!userProjectRepository.existsByHealthUserAndProject(user, project)) {
            UserProject userProject = UserProject.builder()
                .healthUser(user)
                .project(project)
                .build();
            userProjectRepository.save(userProject);
        }

        // 4. 헬스 데이터 저장
        healthDataRepository.save(healthData.measurements().toEntity(user, project));
    }
}