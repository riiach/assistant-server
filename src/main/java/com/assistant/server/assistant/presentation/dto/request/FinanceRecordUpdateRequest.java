package com.assistant.server.assistant.presentation.dto.request;

import com.assistant.server.assistant.domain.enums.FinanceType;

import java.time.LocalDateTime;

public record FinanceRecordUpdateRequest(
        FinanceType type,
        Integer amount,
        String category,
        String memo,
        LocalDateTime occurredAt
) {
}