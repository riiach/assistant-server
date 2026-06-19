package com.assistant.server.assistant.presentation;

import com.assistant.server.assistant.application.AssistantMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assistant")
public class AssistantQueryController {
    private final AssistantMessageService assistantMessageService;
    @GetMapping("/morning-scrum") public String morningScrum() { return assistantMessageService.createMorningScrumMessage(); }
    @GetMapping("/evening-retrospective") public String eveningRetrospective() { return assistantMessageService.createEveningRetrospectiveMessage(); }
}
