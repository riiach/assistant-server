package com.assistant.server.assistant.domain.repository;

import com.assistant.server.assistant.domain.entity.SavingsGoal;
import com.assistant.server.assistant.domain.enums.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {

    List<SavingsGoal> findByStatus(RecordStatus status);
}
