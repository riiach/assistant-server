package com.assistant.server.assistant.presentation.dto.response;

import com.assistant.server.assistant.domain.entity.FinanceRecord;
import com.assistant.server.assistant.domain.enums.FinanceType;

import java.time.LocalDateTime;

public record FinanceRecordResponse(
        Long id,
        FinanceType type,
        Integer amount,
        String category,
        String memo,
        LocalDateTime occurredAt,
        LocalDateTime createdAt
) {
    public static FinanceRecordResponse from(FinanceRecord record) {
        return new FinanceRecordResponse(
                record.getId(),
                record.getType(),
                record.getAmount(),
                record.getCategory(),
                record.getMemo(),
                record.getOccurredAt(),
                record.getCreatedAt()
        );
    }
}