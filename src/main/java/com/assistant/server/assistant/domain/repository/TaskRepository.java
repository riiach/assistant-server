package com.assistant.server.assistant.domain.repository;

import com.assistant.server.assistant.domain.entity.Task;
import com.assistant.server.assistant.domain.enums.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    List<Task> findByStatus(RecordStatus status);

    List<Task> findByDueAtBetween(LocalDateTime start, LocalDateTime end);

    List<Task> findByProjectIdAndStatus(Long projectId, RecordStatus status);
}