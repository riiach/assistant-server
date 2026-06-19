package com.assistant.server.assistant.presentation;

import com.assistant.server.assistant.application.AssistantSettingService;
import com.assistant.server.assistant.presentation.dto.request.AssistantSettingUpdateRequest;
import com.assistant.server.assistant.presentation.dto.response.AssistantSettingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/settings")
public class AssistantSettingController {

    private final AssistantSettingService assistantSettingService;

    @GetMapping
    public AssistantSettingResponse get() {
        return assistantSettingService.get();
    }

    @PutMapping
    public AssistantSettingResponse update(@RequestBody AssistantSettingUpdateRequest request) {
        return assistantSettingService.update(request);
    }
}
