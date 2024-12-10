package com.sungjin.airquailitymonitordemo.service;

import com.sungjin.airquailitymonitordemo.dto.request.ProjectEditRequestDto;
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
import java.time.LocalDateTime;

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

    public ProjectResponseDto updateProject(Long projectId, ProjectEditRequestDto request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));

        project.setProjectName(request.projectName());
        project.setDescription(request.description());

        Project updatedProject = projectRepository.save(project);
        return convertToDto(updatedProject);
    }

    public ProjectResponseDto registerProject(ProjectEditRequestDto request) {
        Project project = new Project();
        project.setProjectName(request.projectName());
        project.setDescription(request.description());
        project.setCreatedAt(LocalDateTime.now());

        Project savedProject = projectRepository.save(project);
        return convertToDto(savedProject);
    }
}