package com.assistant.server.assistant.domain.repository;

import com.assistant.server.assistant.domain.entity.AssistantLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AssistantLogRepository extends JpaRepository<AssistantLog, Long> {

    List<AssistantLog> findByIntent(String intent);

    List<AssistantLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}