package com.assistant.server.assistant.presentation;

import com.assistant.server.assistant.application.TaskService;
import com.assistant.server.assistant.presentation.dto.request.TaskCreateRequest;
import com.assistant.server.assistant.presentation.dto.request.TaskUpdateRequest;
import com.assistant.server.assistant.presentation.dto.response.TaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public Long create(@RequestBody TaskCreateRequest request) {
        return taskService.create(request);
    }

    @GetMapping
    public List<TaskResponse> getAll() {
        return taskService.getAll();
    }

    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable Long id) {
        return taskService.getById(id);
    }

    @PutMapping("/{id}")
    public Long update(
            @PathVariable Long id,
            @RequestBody TaskUpdateRequest request
    ) {
        return taskService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }
}