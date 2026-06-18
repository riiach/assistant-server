package com.assistant.server.assistant.presentation;

import com.assistant.server.assistant.application.AssistantMessageService;
import com.assistant.server.assistant.presentation.dto.request.AssistantMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assistant/messages")
public class AssistantMessageController {

    private final AssistantMessageService assistantMessageService;

    @PostMapping
    public String handleMessage(@RequestBody AssistantMessageRequest request) {
        return assistantMessageService.handleMessage(request.message());
    }
}