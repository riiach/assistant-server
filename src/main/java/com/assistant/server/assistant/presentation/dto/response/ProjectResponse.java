package com.assistant.server.assistant.presentation.dto.response;

import com.assistant.server.assistant.domain.entity.Project;
import com.assistant.server.assistant.domain.enums.RecordStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        String priority,
        LocalDate startDate,
        LocalDate targetDate,
        RecordStatus status,
        LocalDateTime createdAt
) {
    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getPriority(),
                project.getStartDate(),
                project.getTargetDate(),
                project.getStatus(),
                project.getCreatedAt()
        );
    }
}