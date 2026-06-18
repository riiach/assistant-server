package com.assistant.server.assistant.domain.repository;

import com.assistant.server.assistant.domain.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    List<Milestone> findByGoalId(Long goalId);

    List<Milestone> findByGoalIdAndCompleted(Long goalId, Boolean completed);
}
