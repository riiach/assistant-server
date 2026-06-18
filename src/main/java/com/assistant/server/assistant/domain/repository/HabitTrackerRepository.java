package com.assistant.server.assistant.domain.repository;

import com.assistant.server.assistant.domain.entity.HabitTracker;
import com.assistant.server.assistant.domain.enums.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitTrackerRepository extends JpaRepository<HabitTracker, Long> {

    List<HabitTracker> findByStatus(RecordStatus status);
}