package com.assistant.server.assistant.domain.repository;

import com.assistant.server.assistant.domain.entity.Goal;
import com.assistant.server.assistant.domain.enums.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByStatus(RecordStatus status);

    List<Goal> findByTargetYear(Integer targetYear);

    List<Goal> findByTargetYearAndTargetMonth(Integer targetYear, Integer targetMonth);
}
