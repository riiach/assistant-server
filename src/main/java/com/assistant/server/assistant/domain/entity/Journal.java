package com.assistant.server.assistant.domain.entity;

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
public class Journal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mood;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDate journalDate;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.journalDate == null) this.journalDate = LocalDate.now();
    }

    public void update(String mood, String content, java.time.LocalDate journalDate) {
        this.mood = mood; this.content = content; this.journalDate = journalDate;
    }
}
