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
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String priority;

    private LocalDate startDate;

    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    private RecordStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = RecordStatus.IN_PROGRESS;
    }

    public void update(
            String name,
            String description,
            String priority,
            LocalDate startDate,
            LocalDate targetDate,
            RecordStatus status
    ) {
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.startDate = startDate;
        this.targetDate = targetDate;
        this.status = status;
    }
}