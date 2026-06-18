package com.assistant.server.assistant.presentation.dto.request;

import java.time.LocalDateTime;

public record WorkoutRecordCreateRequest(
        String title,
        Integer durationMinutes,
        Integer caloriesBurned,
        String memo,
        LocalDateTime performedAt
) {
}