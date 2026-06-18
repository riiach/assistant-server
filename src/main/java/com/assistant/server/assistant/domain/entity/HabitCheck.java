package com.assistant.server.assistant.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class HabitCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long habitTrackerId;

    private LocalDate checkedDate;

    private Boolean completed;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.checkedDate == null) this.checkedDate = LocalDate.now();
        if (this.completed == null) this.completed = false;
    }

    public void update(
            Long habitTrackerId,
            LocalDate checkedDate,
            Boolean completed
    ) {
        this.habitTrackerId = habitTrackerId;
        this.checkedDate = checkedDate;
        this.completed = completed;
    }
}
