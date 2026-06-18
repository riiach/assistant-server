package com.assistant.server.assistant.presentation.dto.request;

public record AssistantLogCreateRequest(
        String rawMessage,
        String intent,
        String parsedResult
) {
}