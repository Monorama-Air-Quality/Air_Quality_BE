package com.sungjin.airquailitymonitordemo.dto.response;

import com.sungjin.airquailitymonitordemo.dto.response.ProjectResponseDto;

import java.util.List;

public record ProjectListResponseDto(
        List<ProjectResponseDto> projects
) {}