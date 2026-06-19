package com.assistant.server.calendar.infrastructure;

import java.time.LocalDateTime;

public record CalendarEventSummary(
        String id,
        String title,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String description,
        String location
) {
}
