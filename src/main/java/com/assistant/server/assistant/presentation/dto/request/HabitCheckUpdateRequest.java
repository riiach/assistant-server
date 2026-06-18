package com.assistant.server.assistant.presentation.dto.request;

import java.time.LocalDate;

public record HabitCheckUpdateRequest(
        Long habitTrackerId,
        LocalDate checkedDate,
        Boolean completed
) {
}