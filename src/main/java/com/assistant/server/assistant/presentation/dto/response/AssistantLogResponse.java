package com.assistant.server.assistant.presentation.dto.response;

import com.assistant.server.assistant.domain.entity.AssistantLog;

import java.time.LocalDateTime;

public record AssistantLogResponse(
        Long id,
        String rawMessage,
        String intent,
        String parsedResult,
        LocalDateTime createdAt
) {
    public static AssistantLogResponse from(AssistantLog log) {
        return new AssistantLogResponse(
                log.getId(),
                log.getRawMessage(),
                log.getIntent(),
                log.getParsedResult(),
                log.getCreatedAt()
        );
    }
}