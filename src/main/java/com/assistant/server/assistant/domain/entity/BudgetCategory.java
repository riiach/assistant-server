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
public class BudgetCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
    private Integer monthlyLimit;
    private Integer alertThresholdPercent;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.alertThresholdPercent == null) this.alertThresholdPercent = 90;
    }

    public void update(String category, Integer monthlyLimit, Integer alertThresholdPercent) {
        this.category = category; this.monthlyLimit = monthlyLimit; this.alertThresholdPercent = alertThresholdPercent;
    }
}
