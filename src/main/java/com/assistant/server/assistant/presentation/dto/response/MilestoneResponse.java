package com.assistant.server.assistant.presentation.dto.response;

import com.assistant.server.assistant.domain.entity.Milestone;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MilestoneResponse(
        Long id,
        Long goalId,
        String title,
        LocalDate dueDate,
        Boolean completed,
        LocalDateTime createdAt
) {
    public static MilestoneResponse from(Milestone milestone) {
        return new MilestoneResponse(
                milestone.getId(),
                milestone.getGoalId(),
                milestone.getTitle(),
                milestone.getDueDate(),
                milestone.getCompleted(),
                milestone.getCreatedAt()
        );
    }
}