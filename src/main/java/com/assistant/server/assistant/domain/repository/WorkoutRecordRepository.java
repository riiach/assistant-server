package com.assistant.server.assistant.domain.repository;

import com.assistant.server.assistant.domain.entity.WorkoutRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkoutRecordRepository extends JpaRepository<WorkoutRecord, Long> {

    List<WorkoutRecord> findByPerformedAtBetween(LocalDateTime start, LocalDateTime end);
}