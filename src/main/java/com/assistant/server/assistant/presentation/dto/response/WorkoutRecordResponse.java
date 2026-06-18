package com.assistant.server.assistant.presentation.dto.response;

import com.assistant.server.assistant.domain.entity.WorkoutRecord;

import java.time.LocalDateTime;

public record WorkoutRecordResponse(
        Long id,
        String title,
        Integer durationMinutes,
        Integer caloriesBurned,
        String memo,
        LocalDateTime performedAt,
        LocalDateTime createdAt
) {
    public static WorkoutRecordResponse from(WorkoutRecord record) {
        return new WorkoutRecordResponse(
                record.getId(),
                record.getTitle(),
                record.getDurationMinutes(),
                record.getCaloriesBurned(),
                record.getMemo(),
                record.getPerformedAt(),
                record.getCreatedAt()
        );
    }
}