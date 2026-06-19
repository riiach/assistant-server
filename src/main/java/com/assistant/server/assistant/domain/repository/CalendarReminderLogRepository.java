package com.assistant.server.assistant.domain.repository;

import com.assistant.server.assistant.domain.entity.CalendarReminderLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface CalendarReminderLogRepository extends JpaRepository<CalendarReminderLog, Long> {
    boolean existsByCalendarEventIdAndReminderTargetAt(String calendarEventId, LocalDateTime reminderTargetAt);
}
