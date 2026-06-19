package com.assistant.server.assistant.domain.entity;

import com.assistant.server.assistant.domain.enums.RecordStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class HabitTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private RecordStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = RecordStatus.IN_PROGRESS;
    }

    public void update(String title, String description, com.assistant.server.assistant.domain.enums.RecordStatus status) {
        this.title = title; this.description = description; this.status = status;
    }
}
