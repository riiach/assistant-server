package com.assistant.server.slack.application;

import com.assistant.server.assistant.application.AssistantMessageService;
import com.assistant.server.slack.infrastructure.SlackResponseClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SlackCommandService {

    private final AssistantMessageService assistantMessageService;
    private final SlackResponseClient slackResponseClient;

    @Async
    public void handleCommandAsync(String text, String responseUrl) {
        try {
            String result = assistantMessageService.handleMessage(text);
            slackResponseClient.sendResponseUrl(responseUrl, result);
        } catch (Exception e) {
            slackResponseClient.sendResponseUrl(responseUrl, "처리 중 오류가 발생했어. 로그를 확인해줘.\n" + e.getMessage());
        }
    }
}
