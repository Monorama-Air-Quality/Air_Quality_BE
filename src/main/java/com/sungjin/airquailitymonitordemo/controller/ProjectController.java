package com.sungjin.airquailitymonitordemo.controller;

import com.sungjin.airquailitymonitordemo.dto.response.ProjectListResponseDto;
import com.sungjin.airquailitymonitordemo.dto.response.ProjectResponseDto;
import com.sungjin.airquailitymonitordemo.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@Slf4j
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("")
    public ResponseEntity<ProjectListResponseDto> getProjectList() {
        try {
            ProjectListResponseDto response = projectService.getProjectList();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in getProjectList: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}