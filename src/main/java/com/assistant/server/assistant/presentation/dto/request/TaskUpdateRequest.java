package com.assistant.server.assistant.presentation.dto.request;

import com.assistant.server.assistant.domain.enums.RecordStatus;

import java.time.LocalDateTime;

public record TaskUpdateRequest(
        Long projectId,
        String title,
        String memo,
        LocalDateTime dueAt,
        RecordStatus status
) {
}