package com.assistant.server.assistant.presentation.dto.request;

import java.time.LocalTime;

public record AssistantSettingUpdateRequest(
        LocalTime morningScrumTime,
        LocalTime eveningReviewTime,
        Boolean morningScrumEnabled,
        Boolean eveningReviewEnabled,
        Boolean calendarReadEnabled,
        Boolean calendarReminderEnabled,
        Integer eventReminderMinutes,
        String timezone,
        String slackChannelId
) {
}
