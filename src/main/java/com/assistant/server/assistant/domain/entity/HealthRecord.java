package com.assistant.server.assistant.domain.entity;

import com.assistant.server.assistant.domain.enums.HealthType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class HealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private HealthType type;

    private Double value;

    private String unit;

    @Column(columnDefinition = "TEXT")
    private String memo;

    private LocalDate recordedDate;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.recordedDate == null) this.recordedDate = LocalDate.now();
    }

    public void update(
            HealthType type,
            Double value,
            String unit,
            String memo,
            LocalDate recordedDate
    ) {
        this.type = type;
        this.value = value;
        this.unit = unit;
        this.memo = memo;
        this.recordedDate = recordedDate;
    }
}