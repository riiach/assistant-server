package com.assistant.server.assistant.application;

import com.assistant.server.assistant.domain.entity.Project;
import com.assistant.server.assistant.domain.repository.ProjectRepository;
import com.assistant.server.assistant.presentation.dto.request.ProjectCreateRequest;
import com.assistant.server.assistant.presentation.dto.request.ProjectUpdateRequest;
import com.assistant.server.assistant.presentation.dto.response.ProjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public Long create(ProjectCreateRequest request) {
        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .priority(request.priority())
                .startDate(request.startDate())
                .targetDate(request.targetDate())
                .status(request.status())
                .build();

        return projectRepository.save(project).getId();
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getAll() {
        return projectRepository.findAll()
                .stream()
                .map(ProjectResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        return ProjectResponse.from(project);
    }

    @Transactional
    public Long update(Long id, ProjectUpdateRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        project.update(
                request.name(),
                request.description(),
                request.priority(),
                request.startDate(),
                request.targetDate(),
                request.status()
        );

        return project.getId();
    }

    @Transactional
    public void delete(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        projectRepository.delete(project);
    }
}