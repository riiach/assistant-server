package com.assistant.server.assistant.domain.repository;

import com.assistant.server.assistant.domain.entity.FinanceRecord;
import com.assistant.server.assistant.domain.enums.FinanceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FinanceRecordRepository extends JpaRepository<FinanceRecord, Long> {

    List<FinanceRecord> findByType(FinanceType type);

    List<FinanceRecord> findByOccurredAtBetween(LocalDateTime start, LocalDateTime end);

    List<FinanceRecord> findByTypeAndOccurredAtBetween(
            FinanceType type,
            LocalDateTime start,
            LocalDateTime end
    );
}