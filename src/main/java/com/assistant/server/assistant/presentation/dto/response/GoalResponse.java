package com.assistant.server.assistant.presentation.dto.response;

import com.assistant.server.assistant.domain.entity.Goal;
import com.assistant.server.assistant.domain.enums.RecordStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record GoalResponse(
        Long id,
        String title,
        String description,
        String type,
        Integer targetYear,
        Integer targetMonth,
        LocalDate targetDate,
        Integer progress,
        RecordStatus status,
        LocalDateTime createdAt
) {
    public static GoalResponse from(Goal goal) {
        return new GoalResponse(
                goal.getId(),
                goal.getTitle(),
                goal.getDescription(),
                goal.getType(),
                goal.getTargetYear(),
                goal.getTargetMonth(),
                goal.getTargetDate(),
                goal.getProgress(),
                goal.getStatus(),
                goal.getCreatedAt()
        );
    }
}