package com.assistant.server.slack.presentation;

import com.assistant.server.slack.application.SlackCommandService;
import com.assistant.server.slack.presentation.dto.request.SlackSlashCommandRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/slack")
public class SlackCommandController {

    private final SlackCommandService slackCommandService;

    @PostMapping("/commands")
    public String handleCommand(@ModelAttribute SlackSlashCommandRequest request) {
        slackCommandService.handleCommand(request);

        return "기록 중이야. 잠시만 기다려줘 ✨";
    }
}