package com.assistant.server.calendar.infrastructure;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class GoogleCalendarClient {
    public String createEvent(String title, LocalDateTime startAt, LocalDateTime endAt, String description) {
        // Placeholder: connect Google Calendar OAuth/service account here.
        // Current MVP keeps schedule parsing ready but does not call Calendar until credentials are added.
        return "GOOGLE_CALENDAR_PLACEHOLDER";
    }
}
