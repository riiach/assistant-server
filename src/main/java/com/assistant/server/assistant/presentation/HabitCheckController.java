package com.assistant.server.assistant.presentation;

import com.assistant.server.assistant.application.HabitCheckService;
import com.assistant.server.assistant.presentation.dto.request.HabitCheckCreateRequest;
import com.assistant.server.assistant.presentation.dto.request.HabitCheckUpdateRequest;
import com.assistant.server.assistant.presentation.dto.response.HabitCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/habit-checks")
public class HabitCheckController {

    private final HabitCheckService habitCheckService;

    @PostMapping
    public Long create(@RequestBody HabitCheckCreateRequest request) {
        return habitCheckService.create(request);
    }

    @GetMapping
    public List<HabitCheckResponse> getAll() {
        return habitCheckService.getAll();
    }

    @GetMapping("/{id}")
    public HabitCheckResponse getById(@PathVariable Long id) {
        return habitCheckService.getById(id);
    }

    @PutMapping("/{id}")
    public Long update(
            @PathVariable Long id,
            @RequestBody HabitCheckUpdateRequest request
    ) {
        return habitCheckService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        habitCheckService.delete(id);
    }
}