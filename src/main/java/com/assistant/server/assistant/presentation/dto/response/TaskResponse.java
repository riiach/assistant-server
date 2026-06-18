package com.assistant.server.assistant.presentation.dto.response;

import com.assistant.server.assistant.domain.entity.Task;
import com.assistant.server.assistant.domain.enums.RecordStatus;

import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        Long projectId,
        String title,
        String memo,
        LocalDateTime dueAt,
        RecordStatus status,
        LocalDateTime createdAt
) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getProjectId(),
                task.getTitle(),
                task.getMemo(),
                task.getDueAt(),
                task.getStatus(),
                task.getCreatedAt()
        );
    }
}