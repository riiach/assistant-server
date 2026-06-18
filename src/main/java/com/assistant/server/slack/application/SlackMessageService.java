package com.assistant.server.slack.application;

import com.assistant.server.assistant.domain.entity.AssistantLog;
import com.assistant.server.assistant.domain.repository.AssistantLogRepository;
import com.assistant.server.slack.presentation.dto.request.SlackMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SlackMessageService {

    private final AssistantLogRepository assistantLogRepository;

    @Transactional
    public String handleMessage(SlackMessageRequest request) {
        assistantLogRepository.save(
                AssistantLog.builder()
                        .rawMessage(request.text())
                        .intent("SLACK_MESSAGE")
                        .parsedResult(null)
                        .build()
        );

        return "메시지 저장 완료: " + request.text();
    }
}