package com.assistant.server.gemini.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ParsedAssistantResponse(
        String mode,
        String answer,
        List<ParsedFinance> finances,
        List<ParsedHealthRecord> healthRecords,
        List<ParsedWorkout> workouts,
        List<ParsedTask> tasks,
        List<ParsedGoal> goals,
        List<ParsedMilestone> milestones,
        List<ParsedProject> projects,
        List<ParsedHabitTracker> habitTrackers,
        List<ParsedJournal> journals,
        List<ParsedSavingsGoal> savingsGoals,
        List<ParsedBudgetCategory> budgetCategories,
        List<ParsedFinanceSummary> financeSummaries,
        List<ParsedCalendarEvent> calendarEvents
) {
    public record ParsedFinance(String type, Integer amount, String category, String memo) {}
    public record ParsedHealthRecord(String type, Double value, String unit, String memo) {}
    public record ParsedWorkout(String title, Integer durationMinutes, Integer caloriesBurned, String memo) {}
    public record ParsedTask(Long projectId, String title, String memo, LocalDateTime dueAt, String status) {}
    public record ParsedGoal(String title, String description, String type, Integer targetYear, Integer targetMonth, LocalDate targetDate, Integer progress, String status) {}
    public record ParsedMilestone(Long goalId, String title, LocalDate dueDate, Boolean completed) {}
    public record ParsedProject(String name, String description, String priority, LocalDate startDate, LocalDate targetDate, String status) {}
    public record ParsedHabitTracker(String title, String description, String status) {}
    public record ParsedJournal(String mood, String content, LocalDate journalDate) {}
    public record ParsedSavingsGoal(String title, Integer targetAmount, Integer currentAmount, LocalDate targetDate, String status) {}
    public record ParsedBudgetCategory(String category, Integer monthlyLimit, Integer alertThresholdPercent) {}
    public record ParsedFinanceSummary(Integer targetYear, Integer targetMonth, Integer monthlyBudget, Integer monthlyIncomeTarget, Integer monthlySavingTarget) {}
    public record ParsedCalendarEvent(String title, LocalDateTime startAt, LocalDateTime endAt, String description) {}
}
