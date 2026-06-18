package com.assistant.server.assistant.presentation.dto.response;

import com.assistant.server.assistant.domain.entity.HealthRecord;
import com.assistant.server.assistant.domain.enums.HealthType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record HealthRecordResponse(
        Long id,
        HealthType type,
        Double value,
        String unit,
        String memo,
        LocalDate recordedDate,
        LocalDateTime createdAt
) {
    public static HealthRecordResponse from(HealthRecord record) {
        return new HealthRecordResponse(
                record.getId(),
                record.getType(),
                record.getValue(),
                record.getUnit(),
                record.getMemo(),
                record.getRecordedDate(),
                record.getCreatedAt()
        );
    }
}