package com.assistant.server.assistant.presentation;

import com.assistant.server.assistant.application.ProjectService;
import com.assistant.server.assistant.presentation.dto.request.ProjectCreateRequest;
import com.assistant.server.assistant.presentation.dto.request.ProjectUpdateRequest;
import com.assistant.server.assistant.presentation.dto.response.ProjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public Long create(@RequestBody ProjectCreateRequest request) {
        return projectService.create(request);
    }

    @GetMapping
    public List<ProjectResponse> getAll() {
        return projectService.getAll();
    }

    @GetMapping("/{id}")
    public ProjectResponse getById(@PathVariable Long id) {
        return projectService.getById(id);
    }

    @PutMapping("/{id}")
    public Long update(
            @PathVariable Long id,
            @RequestBody ProjectUpdateRequest request
    ) {
        return projectService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        projectService.delete(id);
    }
}