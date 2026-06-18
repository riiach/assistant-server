package com.assistant.server.assistant.presentation.dto.request;

import com.assistant.server.assistant.domain.enums.RecordStatus;

public record HabitTrackerCreateRequest(
        String title,
        String description,
        RecordStatus status
) {
}