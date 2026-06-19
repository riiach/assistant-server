package com.assistant.server.assistant.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AssistantSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalTime morningScrumTime;
    private LocalTime eveningReviewTime;

    private Boolean morningScrumEnabled;
    private Boolean eveningReviewEnabled;
    private Boolean calendarReadEnabled;
    private Boolean calendarReminderEnabled;

    private Integer eventReminderMinutes;
    private String timezone;
    private String slackChannelId;

    private LocalDate lastMorningScrumSentDate;
    private LocalDate lastEveningReviewSentDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.morningScrumTime == null) this.morningScrumTime = LocalTime.of(9, 0);
        if (this.eveningReviewTime == null) this.eveningReviewTime = LocalTime.of(21, 0);
        if (this.morningScrumEnabled == null) this.morningScrumEnabled = true;
        if (this.eveningReviewEnabled == null) this.eveningReviewEnabled = true;
        if (this.calendarReadEnabled == null) this.calendarReadEnabled = true;
        if (this.calendarReminderEnabled == null) this.calendarReminderEnabled = true;
        if (this.eventReminderMinutes == null) this.eventReminderMinutes = 10;
        if (this.timezone == null || this.timezone.isBlank()) this.timezone = "Asia/Seoul";
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void update(LocalTime morningScrumTime,
                       LocalTime eveningReviewTime,
                       Boolean morningScrumEnabled,
                       Boolean eveningReviewEnabled,
                       Boolean calendarReadEnabled,
                       Boolean calendarReminderEnabled,
                       Integer eventReminderMinutes,
                       String timezone,
                       String slackChannelId) {
        if (morningScrumTime != null) this.morningScrumTime = morningScrumTime;
        if (eveningReviewTime != null) this.eveningReviewTime = eveningReviewTime;
        if (morningScrumEnabled != null) this.morningScrumEnabled = morningScrumEnabled;
        if (eveningReviewEnabled != null) this.eveningReviewEnabled = eveningReviewEnabled;
        if (calendarReadEnabled != null) this.calendarReadEnabled = calendarReadEnabled;
        if (calendarReminderEnabled != null) this.calendarReminderEnabled = calendarReminderEnabled;
        if (eventReminderMinutes != null) this.eventReminderMinutes = eventReminderMinutes;
        if (timezone != null && !timezone.isBlank()) this.timezone = timezone;
        if (slackChannelId != null) this.slackChannelId = slackChannelId;
    }

    public void markMorningSent(LocalDate date) {
        this.lastMorningScrumSentDate = date;
    }

    public void markEveningSent(LocalDate date) {
        this.lastEveningReviewSentDate = date;
    }
}
