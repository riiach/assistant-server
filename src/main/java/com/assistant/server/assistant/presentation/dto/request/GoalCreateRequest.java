package com.assistant.server.assistant.presentation.dto.request;

import com.assistant.server.assistant.domain.enums.RecordStatus;

import java.time.LocalDate;

public record GoalCreateRequest(
        String title,
        String description,
        String type,
        Integer targetYear,
        Integer targetMonth,
        LocalDate targetDate,
        Integer progress,
        RecordStatus status
) {
}