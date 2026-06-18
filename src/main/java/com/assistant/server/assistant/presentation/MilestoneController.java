package com.assistant.server.assistant.presentation;

import com.assistant.server.assistant.application.MilestoneService;
import com.assistant.server.assistant.presentation.dto.request.MilestoneCreateRequest;
import com.assistant.server.assistant.presentation.dto.response.MilestoneResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/milestones")
public class MilestoneController {

    private final MilestoneService milestoneService;

    @PostMapping
    public Long create(@RequestBody MilestoneCreateRequest request) {
        return milestoneService.create(request);
    }

    @GetMapping
    public List<MilestoneResponse> getAll() {
        return milestoneService.getAll();
    }

    @GetMapping("/{id}")
    public MilestoneResponse getById(@PathVariable Long id) {
        return milestoneService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        milestoneService.delete(id);
    }
}