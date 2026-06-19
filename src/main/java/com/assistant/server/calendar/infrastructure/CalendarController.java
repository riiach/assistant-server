package com.assistant.server.calendar.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/calendar")
public class CalendarController {

    private final CalendarQueryService calendarQueryService;

    @GetMapping("/today")
    public List<CalendarEventSummary> today() {
        return calendarQueryService.getTodayEvents();
    }

    @GetMapping("/events")
    public List<CalendarEventSummary> events(@RequestParam LocalDate date) {
        return calendarQueryService.getEvents(date);
    }
}
