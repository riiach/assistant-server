package com.assistant.server.slack.presentation;

import com.assistant.server.slack.application.SlackMessageService;
import com.assistant.server.slack.presentation.dto.request.SlackMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/slack")
public class SlackMessageController {

    private final SlackMessageService slackMessageService;

    @PostMapping("/messages")
    public String receiveMessage(@RequestBody SlackMessageRequest request) {
        return slackMessageService.handleMessage(request);
    }
}