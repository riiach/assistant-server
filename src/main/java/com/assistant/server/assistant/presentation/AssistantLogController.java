package com.assistant.server.assistant.presentation;

import com.assistant.server.assistant.application.AssistantLogService;
import com.assistant.server.assistant.presentation.dto.request.AssistantLogCreateRequest;
import com.assistant.server.assistant.presentation.dto.response.AssistantLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assistant-logs")
public class AssistantLogController {

    private final AssistantLogService assistantLogService;

    @PostMapping
    public Long create(@RequestBody AssistantLogCreateRequest request) {
        return assistantLogService.create(request);
    }

    @GetMapping
    public List<AssistantLogResponse> getAll() {
        return assistantLogService.getAll();
    }

    @GetMapping("/{id}")
    public AssistantLogResponse getById(@PathVariable Long id) {
        return assistantLogService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        assistantLogService.delete(id);
    }
}