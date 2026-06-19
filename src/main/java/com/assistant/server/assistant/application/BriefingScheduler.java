package com.assistant.server.assistant.application;

import com.assistant.server.assistant.domain.entity.AssistantSetting;
import com.assistant.server.assistant.domain.entity.CalendarReminderLog;
import com.assistant.server.assistant.domain.repository.AssistantSettingRepository;
import com.assistant.server.assistant.domain.repository.CalendarReminderLogRepository;
import com.assistant.server.calendar.infrastructure.CalendarEventSummary;
import com.assistant.server.calendar.infrastructure.GoogleCalendarClient;
import com.assistant.server.slack.infrastructure.SlackResponseClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BriefingScheduler {

    private final AssistantMessageService assistantMessageService;
    private final AssistantSettingService assistantSettingService;
    private final AssistantSettingRepository assistantSettingRepository;
    private final CalendarReminderLogRepository calendarReminderLogRepository;
    private final GoogleCalendarClient googleCalendarClient;
    private final SlackResponseClient slackResponseClient;

    @Value("${slack.default-channel:}")
    private String defaultChannel;

    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
    @Transactional
    public void runMinuteScheduler() {
        AssistantSetting setting = assistantSettingService.getOrCreate();
        ZoneId zoneId = safeZone(setting.getTimezone());
        LocalDate today = LocalDate.now(zoneId);
        LocalTime now = LocalTime.now(zoneId).withSecond(0).withNano(0);
        String channel = resolveChannel(setting);
        if (channel == null || channel.isBlank()) return;

        if (Boolean.TRUE.equals(setting.getMorningScrumEnabled())
                && sameMinute(now, setting.getMorningScrumTime())
                && !today.equals(setting.getLastMorningScrumSentDate())) {
            slackResponseClient.sendMessage(channel, assistantMessageService.createMorningScrumMessage());
            setting.markMorningSent(today);
            assistantSettingRepository.save(setting);
        }

        if (Boolean.TRUE.equals(setting.getEveningReviewEnabled())
                && sameMinute(now, setting.getEveningReviewTime())
                && !today.equals(setting.getLastEveningReviewSentDate())) {
            slackResponseClient.sendMessage(channel, assistantMessageService.createEveningRetrospectiveMessage());
            setting.markEveningSent(today);
            assistantSettingRepository.save(setting);
        }

        sendCalendarEventReminders(setting, channel, zoneId);
    }

    private void sendCalendarEventReminders(AssistantSetting setting, String channel, ZoneId zoneId) {
        if (!Boolean.TRUE.equals(setting.getCalendarReadEnabled())) return;
        if (!Boolean.TRUE.equals(setting.getCalendarReminderEnabled())) return;
        if (!googleCalendarClient.isConfigured()) return;

        int minutes = setting.getEventReminderMinutes() == null ? 10 : setting.getEventReminderMinutes();
        LocalDateTime from = LocalDateTime.now(zoneId).plusMinutes(minutes).withSecond(0).withNano(0);
        LocalDateTime to = from.plusMinutes(1);
        List<CalendarEventSummary> events = googleCalendarClient.getUpcomingEvents(from, to, zoneId);

        for (CalendarEventSummary event : events) {
            if (event.id() == null || event.startAt() == null) continue;
            if (calendarReminderLogRepository.existsByCalendarEventIdAndReminderTargetAt(event.id(), event.startAt())) continue;

            String text = "⏰ 곧 일정이 시작돼\n" +
                    "*" + event.title() + "*\n" +
                    "시작: " + event.startAt().format(DateTimeFormatter.ofPattern("HH:mm")) + "\n" +
                    (event.location() == null || event.location().isBlank() ? "" : "장소: " + event.location() + "\n") +
                    "지금 준비하면 좋아.";
            slackResponseClient.sendMessage(channel, text);
            calendarReminderLogRepository.save(CalendarReminderLog.builder()
                    .calendarEventId(event.id())
                    .reminderTargetAt(event.startAt())
                    .build());
        }
    }

    private String resolveChannel(AssistantSetting setting) {
        if (setting.getSlackChannelId() != null && !setting.getSlackChannelId().isBlank()) return setting.getSlackChannelId();
        return defaultChannel;
    }

    private boolean sameMinute(LocalTime a, LocalTime b) {
        if (a == null || b == null) return false;
        return a.getHour() == b.getHour() && a.getMinute() == b.getMinute();
    }

    private ZoneId safeZone(String timezone) {
        try { return ZoneId.of(timezone == null || timezone.isBlank() ? "Asia/Seoul" : timezone); }
        catch (Exception e) { return ZoneId.of("Asia/Seoul"); }
    }
}
