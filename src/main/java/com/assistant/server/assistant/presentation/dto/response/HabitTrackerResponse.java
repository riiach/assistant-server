package com.assistant.server.assistant.presentation.dto.response;

import com.assistant.server.assistant.domain.entity.HabitTracker;
import com.assistant.server.assistant.domain.enums.RecordStatus;

import java.time.LocalDateTime;

public record HabitTrackerResponse(
        Long id,
        String title,
        String description,
        RecordStatus status,
        LocalDateTime createdAt
) {
    public static HabitTrackerResponse from(HabitTracker habitTracker) {
        return new HabitTrackerResponse(
                habitTracker.getId(),
                habitTracker.getTitle(),
                habitTracker.getDescription(),
                habitTracker.getStatus(),
                habitTracker.getCreatedAt()
        );
    }
}