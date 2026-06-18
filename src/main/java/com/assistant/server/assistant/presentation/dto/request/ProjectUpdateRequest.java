package com.assistant.server.assistant.presentation.dto.request;

import com.assistant.server.assistant.domain.enums.RecordStatus;

import java.time.LocalDate;

public record ProjectUpdateRequest(
        String name,
        String description,
        String priority,
        LocalDate startDate,
        LocalDate targetDate,
        RecordStatus status
) {
}