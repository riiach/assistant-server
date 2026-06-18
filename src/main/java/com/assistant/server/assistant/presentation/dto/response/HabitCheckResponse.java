package com.assistant.server.assistant.presentation.dto.response;

import com.assistant.server.assistant.domain.entity.HabitCheck;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record HabitCheckResponse(
        Long id,
        Long habitTrackerId,
        LocalDate checkedDate,
        Boolean completed,
        LocalDateTime createdAt
) {
    public static HabitCheckResponse from(HabitCheck habitCheck) {
        return new HabitCheckResponse(
                habitCheck.getId(),
                habitCheck.getHabitTrackerId(),
                habitCheck.getCheckedDate(),
                habitCheck.getCompleted(),
                habitCheck.getCreatedAt()
        );
    }
}