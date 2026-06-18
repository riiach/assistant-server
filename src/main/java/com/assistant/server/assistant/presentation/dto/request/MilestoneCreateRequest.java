package com.assistant.server.assistant.presentation.dto.request;

import java.time.LocalDate;

public record MilestoneCreateRequest(
        Long goalId,
        String title,
        LocalDate dueDate,
        Boolean completed
) {
}