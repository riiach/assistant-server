package com.assistant.server.assistant.domain.repository;

import com.assistant.server.assistant.domain.entity.HealthRecord;
import com.assistant.server.assistant.domain.enums.HealthType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {

    List<HealthRecord> findByType(HealthType type);

    List<HealthRecord> findByRecordedDate(LocalDate recordedDate);

    List<HealthRecord> findByRecordedDateBetween(LocalDate startDate, LocalDate endDate);

    List<HealthRecord> findByTypeAndRecordedDateBetween(
            HealthType type,
            LocalDate startDate,
            LocalDate endDate
    );
}