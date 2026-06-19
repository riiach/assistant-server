package com.assistant.server.assistant.application;

import com.assistant.server.slack.infrastructure.SlackResponseClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BriefingScheduler {

    private final AssistantMessageService assistantMessageService;
    private final SlackResponseClient slackResponseClient;

    @Value("${slack.default-channel:}")
    private String defaultChannel;

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void morningScrum() {
        if (defaultChannel == null || defaultChannel.isBlank()) return;
        slackResponseClient.sendMessage(defaultChannel, assistantMessageService.createMorningScrumMessage());
    }

    @Scheduled(cron = "0 0 21 * * *", zone = "Asia/Seoul")
    public void eveningRetrospective() {
        if (defaultChannel == null || defaultChannel.isBlank()) return;
        slackResponseClient.sendMessage(defaultChannel, assistantMessageService.createEveningRetrospectiveMessage());
    }
}
