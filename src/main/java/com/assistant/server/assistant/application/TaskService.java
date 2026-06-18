package com.assistant.server.assistant.application;

import com.assistant.server.assistant.domain.entity.Task;
import com.assistant.server.assistant.domain.repository.TaskRepository;
import com.assistant.server.assistant.presentation.dto.request.TaskCreateRequest;
import com.assistant.server.assistant.presentation.dto.request.TaskUpdateRequest;
import com.assistant.server.assistant.presentation.dto.response.TaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional
    public Long create(TaskCreateRequest request) {
        Task task = Task.builder()
                .projectId(request.projectId())
                .title(request.title())
                .memo(request.memo())
                .dueAt(request.dueAt())
                .status(request.status())
                .build();

        return taskRepository.save(task).getId();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAll() {
        return taskRepository.findAll()
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        return TaskResponse.from(task);
    }

    @Transactional
    public Long update(Long id, TaskUpdateRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        task.update(
                request.projectId(),
                request.title(),
                request.memo(),
                request.dueAt(),
                request.status()
        );

        return task.getId();
    }

    @Transactional
    public void delete(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        taskRepository.delete(task);
    }
}