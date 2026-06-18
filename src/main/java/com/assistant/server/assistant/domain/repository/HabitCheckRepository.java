package com.assistant.server.assistant.domain.repository;

import com.assistant.server.assistant.domain.entity.HabitCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitCheckRepository extends JpaRepository<HabitCheck, Long> {

    List<HabitCheck> findByHabitTrackerId(Long habitTrackerId);

    List<HabitCheck> findByCheckedDate(LocalDate checkedDate);

    List<HabitCheck> findByCheckedDateBetween(LocalDate startDate, LocalDate endDate);

    Optional<HabitCheck> findByHabitTrackerIdAndCheckedDate(
            Long habitTrackerId,
            LocalDate checkedDate
    );
}