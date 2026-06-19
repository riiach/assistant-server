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
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bankName;
    private String accountName;
    private Long initialBalance;
    private Long currentBalance;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.initialBalance == null) this.initialBalance = 0L;
        if (this.currentBalance == null) this.currentBalance = this.initialBalance;
    }

    public void update(String bankName, String accountName, Long initialBalance, Long currentBalance) {
        this.bankName = bankName; this.accountName = accountName; this.initialBalance = initialBalance; this.currentBalance = currentBalance;
    }
}
