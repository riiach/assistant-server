package com.assistant.server.gemini.presentation;

import com.assistant.server.gemini.infrastructure.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gemini")
public class GeminiTestController {

    private final GeminiService geminiService;

    @GetMapping
    public String test(@RequestParam String message) {
        return geminiService.parseMessage(message);
    }
}