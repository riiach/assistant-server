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
public class WorkoutRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Integer durationMinutes;

    private Integer caloriesBurned;

    @Column(columnDefinition = "TEXT")
    private String memo;

    private LocalDateTime performedAt;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.performedAt == null) this.performedAt = LocalDateTime.now();
    }

    public void update(String title, Integer durationMinutes, Integer caloriesBurned, String memo, java.time.LocalDateTime performedAt) {
        this.title = title; this.durationMinutes = durationMinutes; this.caloriesBurned = caloriesBurned; this.memo = memo; this.performedAt = performedAt;
    }
}
