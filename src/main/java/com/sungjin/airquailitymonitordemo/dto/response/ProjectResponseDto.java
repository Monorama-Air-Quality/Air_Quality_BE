package com.sungjin.airquailitymonitordemo.dto.response;

import java.time.LocalDateTime;

public record ProjectResponseDto(
        Long projectId,
        String projectName,
        String description,
        LocalDateTime createdAt
) {}