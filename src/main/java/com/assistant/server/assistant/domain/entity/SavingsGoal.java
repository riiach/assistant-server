package com.assistant.server.assistant.domain.entity;

import com.assistant.server.assistant.domain.enums.RecordStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Integer targetAmount;

    private Integer currentAmount;

    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    private RecordStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.currentAmount == null) this.currentAmount = 0;
        if (this.status == null) this.status = RecordStatus.IN_PROGRESS;
    }
}