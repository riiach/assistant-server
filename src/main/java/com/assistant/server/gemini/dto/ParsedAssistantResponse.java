package com.assistant.server.gemini.dto;

import java.util.List;

public record ParsedAssistantResponse(
        List<ParsedFinance> finances,
        List<ParsedHealthRecord> healthRecords,
        List<ParsedWorkout> workouts,
        List<ParsedTask> tasks
) {
    public record ParsedFinance(
            String type,
            Integer amount,
            String category,
            String memo
    ) {}

    public record ParsedHealthRecord(
            String type,
            Double value,
            String unit,
            String memo
    ) {}

    public record ParsedWorkout(
            String title,
            Integer durationMinutes,
            Integer caloriesBurned,
            String memo
    ) {}

    public record ParsedTask(
            String title,
            String memo
    ) {}
}