package com.assistant.server.assistant.presentation.dto.request;

import com.assistant.server.assistant.domain.enums.HealthType;

import java.time.LocalDate;

public record HealthRecordUpdateRequest(
        HealthType type,
        Double value,
        String unit,
        String memo,
        LocalDate recordedDate
) {
}