package com.assistant.server.slack.presentation.dto.request;

public record SlackMessageRequest(
        String user,
        String text,
        String channel
) {
}