package com.assistant.server.assistant.domain.entity;

import com.assistant.server.assistant.domain.enums.FinanceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FinanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private FinanceType type;

    private Integer amount;

    private String category;

    @Column(columnDefinition = "TEXT")
    private String memo;

    private LocalDateTime occurredAt;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.occurredAt == null) this.occurredAt = LocalDateTime.now();
    }

    public void update(
            FinanceType type,
            Integer amount,
            String category,
            String memo,
            LocalDateTime occurredAt
    ) {
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.memo = memo;
        this.occurredAt = occurredAt;
    }
}
