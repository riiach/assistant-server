package com.assistant.server.assistant.domain.repository;

import com.assistant.server.assistant.domain.entity.BudgetCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetCategoryRepository extends JpaRepository<BudgetCategory, Long> {
    java.util.Optional<BudgetCategory> findByCategory(String category);
}
