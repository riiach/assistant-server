package com.assistant.server.assistant.presentation.dto.response;

import com.assistant.server.assistant.domain.entity.AssistantSetting;

import java.time.LocalDate;
import java.time.LocalTime;

public record AssistantSettingResponse(
        Long id,
        LocalTime morningScrumTime,
        LocalTime eveningReviewTime,
        Boolean morningScrumEnabled,
        Boolean eveningReviewEnabled,
        Boolean calendarReadEnabled,
        Boolean calendarReminderEnabled,
        Integer eventReminderMinutes,
        String timezone,
        String slackChannelId,
        LocalDate lastMorningScrumSentDate,
        LocalDate lastEveningReviewSentDate
) {
    public static AssistantSettingResponse from(AssistantSetting setting) {
        return new AssistantSettingResponse(
                setting.getId(),
                setting.getMorningScrumTime(),
                setting.getEveningReviewTime(),
                setting.getMorningScrumEnabled(),
                setting.getEveningReviewEnabled(),
                setting.getCalendarReadEnabled(),
                setting.getCalendarReminderEnabled(),
                setting.getEventReminderMinutes(),
                setting.getTimezone(),
                setting.getSlackChannelId(),
                setting.getLastMorningScrumSentDate(),
                setting.getLastEveningReviewSentDate()
        );
    }
}
