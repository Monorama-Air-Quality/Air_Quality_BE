package com.sungjin.airquailitymonitordemo.service;

import com.sungjin.airquailitymonitordemo.dto.response.ProjectListResponseDto;
import com.sungjin.airquailitymonitordemo.dto.response.ProjectResponseDto;
import com.sungjin.airquailitymonitordemo.entity.Project;
import com.sungjin.airquailitymonitordemo.exception.ServiceException;
import com.sungjin.airquailitymonitordemo.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectListResponseDto getProjectList() {
        try {
            List<ProjectResponseDto> projects = projectRepository.findAll()
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return new ProjectListResponseDto(projects);
        } catch (Exception e) {
            log.error("Error while fetching project list: ", e);
            throw new ServiceException("Failed to fetch project list", e);
        }
    }

    private ProjectResponseDto convertToDto(Project project) {
        return new ProjectResponseDto(
                project.getProjectId(),
                project.getProjectName(),
                project.getDescription(),
                project.getCreatedAt()
        );
    }

    public ProjectResponseDto getProjectByDeviceId(String deviceId) {
        Project project = projectRepository.findByDevicesDeviceId(deviceId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found for device ID: " + deviceId));

        return convertToDto(project);
    }
}