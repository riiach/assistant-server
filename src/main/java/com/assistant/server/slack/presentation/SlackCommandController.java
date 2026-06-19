package com.assistant.server.slack.presentation;

import com.assistant.server.slack.application.SlackCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/slack")
public class SlackCommandController {

    private final SlackCommandService slackCommandService;

    @PostMapping("/commands")
    public String handleCommand(
            @RequestParam("text") String text,
            @RequestParam("response_url") String responseUrl
    ) {
        slackCommandService.handleCommandAsync(text, responseUrl);
        return "기록 중이야. 완료되면 바로 알려줄게 ✨";
    }
}
