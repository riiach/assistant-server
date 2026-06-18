package com.assistant.server.assistant.domain.repository;

import com.assistant.server.assistant.domain.entity.Project;
import com.assistant.server.assistant.domain.enums.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByStatus(RecordStatus status);

    List<Project> findByPriority(String priority);
}