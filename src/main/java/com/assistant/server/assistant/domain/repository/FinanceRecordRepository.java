package com.assistant.server.assistant.domain.repository;

import com.assistant.server.assistant.domain.entity.FinanceRecord;
import com.assistant.server.assistant.domain.enums.FinanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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


    @Query("select coalesce(sum(f.amount),0) from FinanceRecord f where f.type = :type and f.occurredAt between :start and :end")
    Integer sumAmountByTypeAndPeriod(@Param("type") FinanceType type, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select coalesce(sum(f.amount),0) from FinanceRecord f where f.type = :type and f.category = :category and f.occurredAt between :start and :end")
    Integer sumAmountByTypeAndCategoryAndPeriod(@Param("type") FinanceType type, @Param("category") String category, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
