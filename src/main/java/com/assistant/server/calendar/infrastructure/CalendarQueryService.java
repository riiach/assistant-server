package com.assistant.server.calendar.infrastructure;

import com.assistant.server.assistant.application.AssistantSettingService;
import com.assistant.server.assistant.domain.entity.AssistantSetting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarQueryService {

    private final GoogleCalendarClient googleCalendarClient;
    private final AssistantSettingService assistantSettingService;

    public List<CalendarEventSummary> getTodayEvents() {
        AssistantSetting setting = assistantSettingService.getOrCreate();
        return googleCalendarClient.getTodayEvents(safeZone(setting.getTimezone()));
    }

    public List<CalendarEventSummary> getEvents(LocalDate date) {
        AssistantSetting setting = assistantSettingService.getOrCreate();
        ZoneId zoneId = safeZone(setting.getTimezone());
        return googleCalendarClient.getEvents(date.atStartOfDay(), date.plusDays(1).atStartOfDay(), zoneId);
    }

    private ZoneId safeZone(String timezone) {
        try { return ZoneId.of(timezone == null || timezone.isBlank() ? "Asia/Seoul" : timezone); }
        catch (Exception e) { return ZoneId.of("Asia/Seoul"); }
    }
}
