package com.assistant.server.assistant.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FinanceSummary {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer targetYear;
    private Integer targetMonth;
    private Integer monthlyBudget;
    private Integer monthlyIncomeTarget;
    private Integer monthlySavingTarget;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }

    public void update(Integer targetYear, Integer targetMonth, Integer monthlyBudget, Integer monthlyIncomeTarget, Integer monthlySavingTarget) {
        this.targetYear = targetYear; this.targetMonth = targetMonth; this.monthlyBudget = monthlyBudget; this.monthlyIncomeTarget = monthlyIncomeTarget; this.monthlySavingTarget = monthlySavingTarget;
    }
}
