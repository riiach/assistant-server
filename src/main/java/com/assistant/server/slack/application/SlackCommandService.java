package com.assistant.server.slack.application;

import com.assistant.server.assistant.application.AssistantMessageService;
import com.assistant.server.slack.infrastructure.SlackResponseClient;
import com.assistant.server.slack.presentation.dto.request.SlackSlashCommandRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SlackCommandService {

    private final AssistantMessageService assistantMessageService;
    private final SlackResponseClient slackResponseClient;

    @Async
    public void handleCommand(SlackSlashCommandRequest request) {
        assistantMessageService.handleMessage(request.text());

        slackResponseClient.sendMessage(
                request.channel_id(),
                "저장 완료했어 ✨\n입력 내용: " + request.text()
        );
    }
}