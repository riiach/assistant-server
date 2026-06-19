package com.assistant.server.assistant.domain.entity;

import com.assistant.server.assistant.domain.enums.RecordStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String type;

    private Integer targetYear;

    private Integer targetMonth;

    private LocalDate targetDate;

    private Integer progress;

    @Enumerated(EnumType.STRING)
    private RecordStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.progress == null) this.progress = 0;
        if (this.status == null) this.status = RecordStatus.IN_PROGRESS;
    }

    public void update(String title, String description, String type, Integer targetYear, Integer targetMonth, java.time.LocalDate targetDate, Integer progress, com.assistant.server.assistant.domain.enums.RecordStatus status) {
        this.title = title; this.description = description; this.type = type; this.targetYear = targetYear; this.targetMonth = targetMonth; this.targetDate = targetDate; this.progress = progress; this.status = status;
    }
}
