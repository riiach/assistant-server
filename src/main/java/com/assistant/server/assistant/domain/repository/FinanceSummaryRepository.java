package com.assistant.server.assistant.domain.repository;

import com.assistant.server.assistant.domain.entity.FinanceSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinanceSummaryRepository extends JpaRepository<FinanceSummary, Long> {
    java.util.Optional<FinanceSummary> findByTargetYearAndTargetMonth(Integer targetYear, Integer targetMonth);
}
