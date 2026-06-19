package com.assistant.server.assistant.domain.repository;

import com.assistant.server.assistant.domain.entity.HealthRecord;
import com.assistant.server.assistant.domain.enums.HealthType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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


    @Query("select coalesce(sum(h.value),0) from HealthRecord h where h.type = :type and h.recordedDate = :date")
    Double sumValueByTypeAndDate(@Param("type") HealthType type, @Param("date") LocalDate date);

    @Query("select coalesce(sum(h.value),0) from HealthRecord h where h.type = :type and h.recordedDate between :startDate and :endDate")
    Double sumValueByTypeAndPeriod(@Param("type") HealthType type, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
