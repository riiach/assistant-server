package com.assistant.server.assistant.presentation;

import com.assistant.server.assistant.application.HabitTrackerService;
import com.assistant.server.assistant.presentation.dto.request.HabitTrackerCreateRequest;
import com.assistant.server.assistant.presentation.dto.response.HabitTrackerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/habit-trackers")
public class HabitTrackerController {

    private final HabitTrackerService habitTrackerService;

    @PostMapping
    public Long create(@RequestBody HabitTrackerCreateRequest request) {
        return habitTrackerService.create(request);
    }

    @GetMapping
    public List<HabitTrackerResponse> getAll() {
        return habitTrackerService.getAll();
    }

    @GetMapping("/{id}")
    public HabitTrackerResponse getById(@PathVariable Long id) {
        return habitTrackerService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        habitTrackerService.delete(id);
    }
}