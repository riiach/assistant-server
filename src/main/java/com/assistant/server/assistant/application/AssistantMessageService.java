package com.assistant.server.assistant.application;

import com.assistant.server.assistant.domain.entity.*;
import com.assistant.server.assistant.domain.enums.FinanceType;
import com.assistant.server.assistant.domain.enums.HealthType;
import com.assistant.server.assistant.domain.enums.RecordStatus;
import com.assistant.server.assistant.domain.repository.*;
import com.assistant.server.gemini.dto.ParsedAssistantResponse;
import com.assistant.server.gemini.infrastructure.GeminiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssistantMessageService {

    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    private final FinanceRecordRepository financeRecordRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final WorkoutRecordRepository workoutRecordRepository;
    private final TaskRepository taskRepository;
    private final AssistantLogRepository assistantLogRepository;
    private final GoalRepository goalRepository;
    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;
    private final HabitTrackerRepository habitTrackerRepository;
    private final JournalRepository journalRepository;
    private final SavingsGoalRepository savingsGoalRepository;
    private final BudgetCategoryRepository budgetCategoryRepository;
    private final FinanceSummaryRepository financeSummaryRepository;

    @Transactional
    public String handleMessage(String message) {
        if (isWaterQuestion(message)) return answerWaterToday();
        if (isFinanceQuestion(message)) return answerMonthlyFinance();
        if (isGoalQuestion(message)) return answerGoals();
        if (isTaskQuestion(message)) return answerTasks();

        ParsedAssistantResponse parsed = geminiService.parseMessageToDto(message);
        saveParsed(message, parsed);

        String budgetWarning = buildBudgetWarning();
        String summary = buildSaveSummary(parsed);
        return budgetWarning.isBlank() ? summary : summary + "\n\n" + budgetWarning;
    }

    @Transactional
    public void saveParsed(String message, ParsedAssistantResponse parsed) {
        saveFinances(parsed);
        saveHealthRecords(parsed);
        saveWorkouts(parsed);
        saveTasks(parsed);
        saveGoals(parsed);
        saveMilestones(parsed);
        saveProjects(parsed);
        saveHabitTrackers(parsed);
        saveJournals(parsed);
        saveSavingsGoals(parsed);
        saveBudgetCategories(parsed);
        saveFinanceSummaries(parsed);
        saveLog(message, parsed);
    }

    @Transactional(readOnly = true)
    public String createMorningScrumMessage() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay().minusNanos(1);
        List<Task> dueToday = taskRepository.findByDueAtBetween(start, end);
        List<Task> todo = taskRepository.findByStatus(RecordStatus.TODO);
        Double water = healthRecordRepository.sumValueByTypeAndDate(HealthType.WATER, today);

        StringBuilder sb = new StringBuilder("🌤️ 오늘의 스크럼\n");
        sb.append("💧 현재 물 섭취: ").append(formatNumber(water)).append("ml\n");
        sb.append("✅ 오늘 마감 할 일: ").append(dueToday.size()).append("개\n");
        dueToday.stream().limit(5).forEach(t -> sb.append("- ").append(t.getTitle()).append("\n"));
        sb.append("📌 전체 TODO: ").append(todo.size()).append("개\n");
        sb.append("오늘도 기록하면서 같이 정리해보자 ✨");
        return sb.toString();
    }

    @Transactional(readOnly = true)
    public String createEveningRetrospectiveMessage() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay().minusNanos(1);
        Integer expense = financeRecordRepository.sumAmountByTypeAndPeriod(FinanceType.EXPENSE, start, end);
        Double water = healthRecordRepository.sumValueByTypeAndDate(HealthType.WATER, today);
        List<WorkoutRecord> workouts = workoutRecordRepository.findByPerformedAtBetween(start, end);
        List<Task> done = taskRepository.findByStatus(RecordStatus.DONE);

        return "🌙 오늘의 회고\n" +
                "💸 오늘 지출: " + nvl(expense) + "원\n" +
                "💧 물 섭취: " + formatNumber(water) + "ml\n" +
                "🏃 운동 기록: " + workouts.size() + "개\n" +
                "✅ 완료된 전체 할 일: " + done.size() + "개\n" +
                "내일은 가장 중요한 일 1개부터 시작하자 ✨";
    }

    private void saveFinances(ParsedAssistantResponse parsed) {
        if (parsed.finances() == null) return;
        parsed.finances().forEach(finance -> financeRecordRepository.save(FinanceRecord.builder()
                .type(enumOrDefault(FinanceType.class, finance.type(), FinanceType.EXPENSE))
                .amount(nvl(finance.amount()))
                .category(emptyToEtc(finance.category()))
                .memo(finance.memo())
                .build()));
    }

    private void saveHealthRecords(ParsedAssistantResponse parsed) {
        if (parsed.healthRecords() == null) return;
        parsed.healthRecords().forEach(health -> healthRecordRepository.save(HealthRecord.builder()
                .type(enumOrDefault(HealthType.class, health.type(), HealthType.MOOD))
                .value(health.value() == null ? 0.0 : health.value())
                .unit(health.unit())
                .memo(health.memo())
                .build()));
    }

    private void saveWorkouts(ParsedAssistantResponse parsed) {
        if (parsed.workouts() == null) return;
        parsed.workouts().forEach(workout -> workoutRecordRepository.save(WorkoutRecord.builder()
                .title(workout.title())
                .durationMinutes(workout.durationMinutes())
                .caloriesBurned(workout.caloriesBurned())
                .memo(workout.memo())
                .build()));
    }

    private void saveTasks(ParsedAssistantResponse parsed) {
        if (parsed.tasks() == null) return;
        parsed.tasks().forEach(task -> taskRepository.save(Task.builder()
                .projectId(task.projectId())
                .title(task.title())
                .memo(task.memo())
                .dueAt(task.dueAt())
                .status(enumOrDefault(RecordStatus.class, task.status(), RecordStatus.TODO))
                .build()));
    }

    private void saveGoals(ParsedAssistantResponse parsed) {
        if (parsed.goals() == null) return;
        parsed.goals().forEach(goal -> goalRepository.save(Goal.builder()
                .title(goal.title())
                .description(goal.description())
                .type(goal.type())
                .targetYear(goal.targetYear())
                .targetMonth(goal.targetMonth())
                .targetDate(goal.targetDate())
                .progress(goal.progress())
                .status(enumOrDefault(RecordStatus.class, goal.status(), RecordStatus.IN_PROGRESS))
                .build()));
    }

    private void saveMilestones(ParsedAssistantResponse parsed) {
        if (parsed.milestones() == null) return;
        parsed.milestones().forEach(m -> milestoneRepository.save(Milestone.builder()
                .goalId(m.goalId())
                .title(m.title())
                .dueDate(m.dueDate())
                .completed(m.completed())
                .build()));
    }

    private void saveProjects(ParsedAssistantResponse parsed) {
        if (parsed.projects() == null) return;
        parsed.projects().forEach(project -> projectRepository.save(Project.builder()
                .name(project.name())
                .description(project.description())
                .priority(project.priority())
                .startDate(project.startDate())
                .targetDate(project.targetDate())
                .status(enumOrDefault(RecordStatus.class, project.status(), RecordStatus.IN_PROGRESS))
                .build()));
    }

    private void saveHabitTrackers(ParsedAssistantResponse parsed) {
        if (parsed.habitTrackers() == null) return;
        parsed.habitTrackers().forEach(h -> habitTrackerRepository.save(HabitTracker.builder()
                .title(h.title())
                .description(h.description())
                .status(enumOrDefault(RecordStatus.class, h.status(), RecordStatus.IN_PROGRESS))
                .build()));
    }

    private void saveJournals(ParsedAssistantResponse parsed) {
        if (parsed.journals() == null) return;
        parsed.journals().forEach(j -> journalRepository.save(Journal.builder()
                .mood(j.mood())
                .content(j.content())
                .journalDate(j.journalDate())
                .build()));
    }

    private void saveSavingsGoals(ParsedAssistantResponse parsed) {
        if (parsed.savingsGoals() == null) return;
        parsed.savingsGoals().forEach(s -> savingsGoalRepository.save(SavingsGoal.builder()
                .title(s.title())
                .targetAmount(s.targetAmount())
                .currentAmount(s.currentAmount())
                .targetDate(s.targetDate())
                .status(enumOrDefault(RecordStatus.class, s.status(), RecordStatus.IN_PROGRESS))
                .build()));
    }

    private void saveBudgetCategories(ParsedAssistantResponse parsed) {
        if (parsed.budgetCategories() == null) return;
        parsed.budgetCategories().forEach(b -> budgetCategoryRepository.save(BudgetCategory.builder()
                .category(emptyToEtc(b.category()))
                .monthlyLimit(b.monthlyLimit())
                .alertThresholdPercent(b.alertThresholdPercent())
                .build()));
    }

    private void saveFinanceSummaries(ParsedAssistantResponse parsed) {
        if (parsed.financeSummaries() == null) return;
        parsed.financeSummaries().forEach(f -> financeSummaryRepository.save(FinanceSummary.builder()
                .targetYear(f.targetYear())
                .targetMonth(f.targetMonth())
                .monthlyBudget(f.monthlyBudget())
                .monthlyIncomeTarget(f.monthlyIncomeTarget())
                .monthlySavingTarget(f.monthlySavingTarget())
                .build()));
    }

    private void saveLog(String message, ParsedAssistantResponse parsed) {
        try {
            assistantLogRepository.save(AssistantLog.builder()
                    .rawMessage(message)
                    .intent(parsed.mode() == null ? "GEMINI_PARSED_MESSAGE" : parsed.mode())
                    .parsedResult(objectMapper.writeValueAsString(parsed))
                    .build());
        } catch (Exception e) { throw new RuntimeException("AssistantLog 저장 실패", e); }
    }

    private String buildSaveSummary(ParsedAssistantResponse p) {
        int f = size(p.finances()), h = size(p.healthRecords()), w = size(p.workouts()), t = size(p.tasks());
        int g = size(p.goals()), pr = size(p.projects()), hb = size(p.habitTrackers()), j = size(p.journals());
        return "저장 완료했어 ✨\n" +
                "가계부 " + f + "개 · 건강 " + h + "개 · 운동 " + w + "개 · 할 일 " + t + "개\n" +
                "목표 " + g + "개 · 프로젝트 " + pr + "개 · 습관 " + hb + "개 · 저널 " + j + "개";
    }

    private String buildBudgetWarning() {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime start = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = now.with(TemporalAdjusters.lastDayOfMonth()).atTime(23,59,59);
        StringBuilder sb = new StringBuilder();
        for (BudgetCategory b : budgetCategoryRepository.findAll()) {
            if (b.getMonthlyLimit() == null || b.getMonthlyLimit() <= 0) continue;
            int spent = nvl(financeRecordRepository.sumAmountByTypeAndCategoryAndPeriod(FinanceType.EXPENSE, b.getCategory(), start, end));
            int percent = (int) Math.round(spent * 100.0 / b.getMonthlyLimit());
            if (percent >= nvl(b.getAlertThresholdPercent())) {
                sb.append("⚠️ ").append(b.getCategory()).append(" 예산 ").append(percent).append("% 사용 중: ")
                        .append(spent).append("/").append(b.getMonthlyLimit()).append("원\n");
            }
        }
        return sb.toString().trim();
    }

    private String answerWaterToday() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        Double water = healthRecordRepository.sumValueByTypeAndDate(HealthType.WATER, today);
        return "오늘 물은 총 " + formatNumber(water) + "ml 마셨어 💧";
    }

    private String answerMonthlyFinance() {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime start = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = now.with(TemporalAdjusters.lastDayOfMonth()).atTime(23,59,59);
        int income = nvl(financeRecordRepository.sumAmountByTypeAndPeriod(FinanceType.INCOME, start, end));
        int expense = nvl(financeRecordRepository.sumAmountByTypeAndPeriod(FinanceType.EXPENSE, start, end));
        int saving = nvl(financeRecordRepository.sumAmountByTypeAndPeriod(FinanceType.SAVING, start, end));
        return "이번 달 정리야 💰\n수입: " + income + "원\n지출: " + expense + "원\n저축: " + saving + "원\n남은 흐름: " + (income - expense - saving) + "원";
    }

    private String answerGoals() {
        List<Goal> goals = goalRepository.findByStatus(RecordStatus.IN_PROGRESS);
        if (goals.isEmpty()) return "진행 중인 목표가 아직 없어. 목표를 말해주면 바로 저장하고 계획을 세워줄게.";
        StringBuilder sb = new StringBuilder("진행 중인 목표야 🎯\n");
        goals.stream().limit(10).forEach(g -> sb.append("- ").append(g.getTitle()).append(" (").append(g.getProgress()).append("%)\n"));
        return sb.toString();
    }

    private String answerTasks() {
        List<Task> tasks = taskRepository.findByStatus(RecordStatus.TODO);
        if (tasks.isEmpty()) return "남은 할 일이 없어 ✨";
        StringBuilder sb = new StringBuilder("남은 할 일이야 ✅\n");
        tasks.stream().limit(10).forEach(t -> sb.append("☐ ").append(t.getTitle()).append("\n"));
        return sb.toString();
    }

    private boolean isWaterQuestion(String m) { return m.contains("물") && (m.contains("얼마") || m.contains("얼마나") || m.contains("몇")); }
    private boolean isFinanceQuestion(String m) { return (m.contains("이번 달") || m.contains("이번달")) && (m.contains("얼마") || m.contains("지출") || m.contains("수입") || m.contains("돈")); }
    private boolean isGoalQuestion(String m) { return m.contains("목표") && (m.contains("알려") || m.contains("뭐") || m.contains("진행")); }
    private boolean isTaskQuestion(String m) { return (m.contains("할일") || m.contains("할 일") || m.contains("태스크")) && (m.contains("뭐") || m.contains("알려") || m.contains("남")); }

    private int size(List<?> list) { return list == null ? 0 : list.size(); }
    private int nvl(Integer value) { return value == null ? 0 : value; }
    private String formatNumber(Double value) { return String.valueOf(value == null ? 0 : Math.round(value)); }
    private String emptyToEtc(String value) { return value == null || value.isBlank() ? "ETC" : value; }

    private <E extends Enum<E>> E enumOrDefault(Class<E> type, String value, E defaultValue) {
        try { return value == null ? defaultValue : Enum.valueOf(type, value.toUpperCase()); }
        catch (Exception e) { return defaultValue; }
    }
}
