package com.assistant.server.assistant.presentation;

import com.assistant.server.assistant.application.GoalService;
import com.assistant.server.assistant.presentation.dto.request.GoalCreateRequest;
import com.assistant.server.assistant.presentation.dto.response.GoalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public Long create(@RequestBody GoalCreateRequest request) {
        return goalService.create(request);
    }

    @GetMapping
    public List<GoalResponse> getAll() {
        return goalService.getAll();
    }

    @GetMapping("/{id}")
    public GoalResponse getById(@PathVariable Long id) {
        return goalService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        goalService.delete(id);
    }
}