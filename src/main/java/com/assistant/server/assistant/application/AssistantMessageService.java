package com.assistant.server.assistant.application;

import com.assistant.server.assistant.domain.entity.*;
import com.assistant.server.assistant.domain.enums.FinanceType;
import com.assistant.server.assistant.domain.enums.HealthType;
import com.assistant.server.assistant.domain.enums.RecordStatus;
import com.assistant.server.assistant.domain.repository.*;
import com.assistant.server.calendar.infrastructure.CalendarEventSummary;
import com.assistant.server.calendar.infrastructure.GoogleCalendarClient;
import com.assistant.server.gemini.dto.ParsedAssistantResponse;
import com.assistant.server.gemini.infrastructure.GeminiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AssistantMessageService {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

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
    private final AssistantSettingService assistantSettingService;
    private final GoogleCalendarClient googleCalendarClient;

    @Transactional
    public String handleMessage(String message) {
        String trimmed = message == null ? "" : message.trim();
        if (trimmed.isBlank()) return "내용을 입력해줘.";

        String directAnswer = answerDirectQuestionIfPossible(trimmed);
        if (directAnswer != null) return directAnswer;

        ParsedAssistantResponse parsed = geminiService.parseMessageToDto(trimmed);

        if (isQueryMode(parsed) || looksLikeQuestion(trimmed)) {
            saveLog(trimmed, parsed);
            return answerQuery(trimmed, parsed);
        }

        int estimatedCalories = estimateCaloriesFromFood(trimmed);
        saveParsed(trimmed, parsed);
        if (estimatedCalories > 0 && !hasCalories(parsed)) {
            saveEstimatedCalories(trimmed, estimatedCalories);
        }

        String budgetWarning = buildBudgetWarning();
        String summary = buildSaveSummary(parsed, estimatedCalories);
        return budgetWarning.isBlank() ? summary : summary + "\n\n" + budgetWarning;
    }

    private String answerDirectQuestionIfPossible(String message) {
        if (isWaterQuestion(message)) return answerWaterToday();
        if (isCalorieQuestion(message)) return answerCaloriesToday();
        if (isFinanceQuestion(message)) return answerMonthlyFinance();
        if (isGoalQuestion(message)) return answerGoals();
        if (isTaskQuestion(message)) return answerTasks();
        if (isScrumSettingQuestion(message)) return answerScrumSettings();
        if (isCalendarQuestion(message)) return answerCalendar(message);
        return null;
    }

    private boolean isQueryMode(ParsedAssistantResponse parsed) {
        return parsed != null && parsed.mode() != null && parsed.mode().equalsIgnoreCase("QUERY");
    }

    private boolean looksLikeQuestion(String message) {
        return message.contains("?") || message.contains("얼마") || message.contains("몇") || message.contains("알려") || message.contains("뭐야") || message.contains("무엇");
    }

    private String answerQuery(String message, ParsedAssistantResponse parsed) {
        String direct = answerDirectQuestionIfPossible(message);
        if (direct != null) return direct;
        if (parsed != null && parsed.answer() != null && !parsed.answer().isBlank()) {
            return parsed.answer() + "\n\n아직 이 질문에 대한 전용 조회 로직은 없어서 저장하지는 않았어.";
        }
        return "이건 기록이 아니라 질문으로 판단했어. 아직 이 질문에 대한 조회 로직은 없어서 DB에는 저장하지 않았어.";
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
        AssistantSetting setting = assistantSettingService.getOrCreate();
        ZoneId zoneId = zone(setting.getTimezone());
        LocalDate today = LocalDate.now(zoneId);
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay().minusNanos(1);
        List<Task> dueToday = taskRepository.findByDueAtBetween(start, end);
        List<Task> todo = taskRepository.findByStatus(RecordStatus.TODO);
        Double water = healthRecordRepository.sumValueByTypeAndDate(HealthType.WATER, today);
        Double calories = healthRecordRepository.sumValueByTypeAndDate(HealthType.CALORIES, today);
        List<CalendarEventSummary> events = calendarEventsForToday(setting);

        StringBuilder sb = new StringBuilder("🌤️ 오늘의 스크럼\n");
        sb.append("오늘 집중해야 할 흐름을 정리해볼게.\n\n");
        appendCalendarSection(sb, events, "📅 오늘 캘린더");
        sb.append("\n💧 현재 물 섭취: ").append(formatNumber(water)).append("ml\n");
        sb.append("🍽️ 현재 섭취 칼로리: ").append(formatNumber(calories)).append("kcal\n");
        sb.append("✅ 오늘 마감 할 일: ").append(dueToday.size()).append("개\n");
        dueToday.stream().limit(7).forEach(t -> sb.append("- ").append(t.getTitle()).append("\n"));
        sb.append("📌 전체 TODO: ").append(todo.size()).append("개\n");
        sb.append("\n오늘은 캘린더에 있는 일정 먼저 지키고, 남는 시간에는 가장 중요한 일 1개만 끝내자 ✨");
        return sb.toString();
    }

    @Transactional(readOnly = true)
    public String createEveningRetrospectiveMessage() {
        AssistantSetting setting = assistantSettingService.getOrCreate();
        ZoneId zoneId = zone(setting.getTimezone());
        LocalDate today = LocalDate.now(zoneId);
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay().minusNanos(1);
        Integer expense = financeRecordRepository.sumAmountByTypeAndPeriod(FinanceType.EXPENSE, start, end);
        Double water = healthRecordRepository.sumValueByTypeAndDate(HealthType.WATER, today);
        Double calories = healthRecordRepository.sumValueByTypeAndDate(HealthType.CALORIES, today);
        List<WorkoutRecord> workouts = workoutRecordRepository.findByPerformedAtBetween(start, end);
        List<Task> done = taskRepository.findByStatus(RecordStatus.DONE);
        List<CalendarEventSummary> events = calendarEventsForToday(setting);

        StringBuilder sb = new StringBuilder("🌙 오늘의 회고\n");
        sb.append("오늘 기록과 캘린더 기준으로 정리해볼게.\n\n");
        appendCalendarSection(sb, events, "📅 오늘 있었던 일정");
        sb.append("\n💸 오늘 지출: ").append(nvl(expense)).append("원\n");
        sb.append("💧 물 섭취: ").append(formatNumber(water)).append("ml\n");
        sb.append("🍽️ 섭취 칼로리: ").append(formatNumber(calories)).append("kcal\n");
        sb.append("🏃 운동 기록: ").append(workouts.size()).append("개\n");
        sb.append("✅ 완료된 전체 할 일: ").append(done.size()).append("개\n");
        sb.append("\n내일은 오늘 미룬 것보다 가장 중요한 것부터 작게 시작하자 ✨");
        return sb.toString();
    }

    private List<CalendarEventSummary> calendarEventsForToday(AssistantSetting setting) {
        if (setting.getCalendarReadEnabled() == null || !setting.getCalendarReadEnabled()) return List.of();
        if (!googleCalendarClient.isConfigured()) return List.of();
        return googleCalendarClient.getTodayEvents(zone(setting.getTimezone()));
    }

    private void appendCalendarSection(StringBuilder sb, List<CalendarEventSummary> events, String title) {
        sb.append(title).append("\n");
        if (events == null || events.isEmpty()) {
            sb.append("- 등록된 일정 없음\n");
            return;
        }
        events.stream().limit(8).forEach(e -> sb.append("- ")
                .append(formatTime(e.startAt()))
                .append(" ")
                .append(e.title())
                .append(e.location() == null || e.location().isBlank() ? "" : " @ " + e.location())
                .append("\n"));
    }

    private String answerCalendar(String message) {
        AssistantSetting setting = assistantSettingService.getOrCreate();
        if (setting.getCalendarReadEnabled() == null || !setting.getCalendarReadEnabled()) return "캘린더 읽기 기능이 설정에서 꺼져 있어.";
        if (!googleCalendarClient.isConfigured()) return "Google Calendar 설정이 아직 안 되어 있어. GOOGLE_CALENDAR_ID, GOOGLE_CLIENT_EMAIL, GOOGLE_PRIVATE_KEY를 설정하고 캘린더를 서비스 계정에 공유해야 해.";
        ZoneId zoneId = zone(setting.getTimezone());
        LocalDate today = LocalDate.now(zoneId);
        LocalDate target = message.contains("내일") ? today.plusDays(1) : today;
        List<CalendarEventSummary> events = googleCalendarClient.getEvents(target.atStartOfDay(), target.plusDays(1).atStartOfDay(), zoneId);
        StringBuilder sb = new StringBuilder((target.equals(today) ? "오늘" : "내일") + " 캘린더 일정이야 📅\n");
        appendCalendarSection(sb, events, "");
        return sb.toString().trim();
    }

    private String answerScrumSettings() {
        AssistantSetting setting = assistantSettingService.getOrCreate();
        return "현재 알림 설정이야 ⚙️\n" +
                "아침 스크럼: " + setting.getMorningScrumTime() + " (" + enabled(setting.getMorningScrumEnabled()) + ")\n" +
                "저녁 회고: " + setting.getEveningReviewTime() + " (" + enabled(setting.getEveningReviewEnabled()) + ")\n" +
                "캘린더 읽기: " + enabled(setting.getCalendarReadEnabled()) + "\n" +
                "일정 시작 전 알림: " + setting.getEventReminderMinutes() + "분 전 (" + enabled(setting.getCalendarReminderEnabled()) + ")";
    }

    private void saveFinances(ParsedAssistantResponse parsed) {
        if (parsed == null || parsed.finances() == null) return;
        parsed.finances().forEach(finance -> financeRecordRepository.save(FinanceRecord.builder()
                .type(enumOrDefault(FinanceType.class, finance.type(), FinanceType.EXPENSE))
                .amount(nvl(finance.amount()))
                .category(emptyToEtc(finance.category()))
                .memo(finance.memo())
                .build()));
    }

    private void saveHealthRecords(ParsedAssistantResponse parsed) {
        if (parsed == null || parsed.healthRecords() == null) return;
        parsed.healthRecords().forEach(health -> healthRecordRepository.save(HealthRecord.builder()
                .type(enumOrDefault(HealthType.class, health.type(), HealthType.MOOD))
                .value(health.value() == null ? 0.0 : health.value())
                .unit(health.unit())
                .memo(health.memo())
                .build()));
    }

    private void saveEstimatedCalories(String message, int estimatedCalories) {
        healthRecordRepository.save(HealthRecord.builder()
                .type(HealthType.CALORIES)
                .value((double) estimatedCalories)
                .unit("kcal")
                .memo("AI 평균 추정: " + message)
                .build());
    }

    private boolean hasCalories(ParsedAssistantResponse parsed) {
        return parsed != null && parsed.healthRecords() != null && parsed.healthRecords().stream()
                .anyMatch(h -> h.type() != null && h.type().equalsIgnoreCase("CALORIES"));
    }

    private int estimateCaloriesFromFood(String message) {
        if (!(message.contains("먹") || message.contains("마셨") || message.contains("마셨어"))) return 0;
        int count = extractCount(message);
        if (count <= 0) count = 1;
        int unitCalories = 0;
        if (message.contains("치킨")) unitCalories = 250;
        else if (message.contains("피자")) unitCalories = 285;
        else if (message.contains("김밥")) unitCalories = 350;
        else if (message.contains("라면")) unitCalories = 500;
        else if (message.contains("밥") || message.contains("공기밥")) unitCalories = 300;
        else if (message.contains("커피") || message.contains("아메리카노")) unitCalories = 10;
        else if (message.contains("라떼")) unitCalories = 180;
        else if (message.contains("빵")) unitCalories = 250;
        else if (message.contains("샐러드")) unitCalories = 180;
        return unitCalories * count;
    }

    private int extractCount(String message) {
        String normalized = message
                .replace("한", "1").replace("두", "2").replace("세", "3").replace("네", "4")
                .replace("다섯", "5").replace("여섯", "6").replace("일곱", "7").replace("여덟", "8")
                .replace("아홉", "9").replace("열", "10");
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d+)\\s*(조각|개|잔|그릇|공기|인분|봉지)").matcher(normalized);
        if (matcher.find()) return Integer.parseInt(matcher.group(1));
        return 1;
    }

    private void saveWorkouts(ParsedAssistantResponse parsed) {
        if (parsed == null || parsed.workouts() == null) return;
        parsed.workouts().forEach(workout -> workoutRecordRepository.save(WorkoutRecord.builder()
                .title(workout.title())
                .durationMinutes(workout.durationMinutes())
                .caloriesBurned(workout.caloriesBurned())
                .memo(workout.memo())
                .build()));
    }

    private void saveTasks(ParsedAssistantResponse parsed) {
        if (parsed == null || parsed.tasks() == null) return;
        parsed.tasks().forEach(task -> taskRepository.save(Task.builder()
                .projectId(task.projectId())
                .title(task.title())
                .memo(task.memo())
                .dueAt(task.dueAt())
                .status(enumOrDefault(RecordStatus.class, task.status(), RecordStatus.TODO))
                .build()));
    }

    private void saveGoals(ParsedAssistantResponse parsed) {
        if (parsed == null || parsed.goals() == null) return;
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
        if (parsed == null || parsed.milestones() == null) return;
        parsed.milestones().forEach(m -> milestoneRepository.save(Milestone.builder()
                .goalId(m.goalId())
                .title(m.title())
                .dueDate(m.dueDate())
                .completed(m.completed())
                .build()));
    }

    private void saveProjects(ParsedAssistantResponse parsed) {
        if (parsed == null || parsed.projects() == null) return;
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
        if (parsed == null || parsed.habitTrackers() == null) return;
        parsed.habitTrackers().forEach(h -> habitTrackerRepository.save(HabitTracker.builder()
                .title(h.title())
                .description(h.description())
                .status(enumOrDefault(RecordStatus.class, h.status(), RecordStatus.IN_PROGRESS))
                .build()));
    }

    private void saveJournals(ParsedAssistantResponse parsed) {
        if (parsed == null || parsed.journals() == null) return;
        parsed.journals().forEach(j -> journalRepository.save(Journal.builder()
                .mood(j.mood())
                .content(j.content())
                .journalDate(j.journalDate())
                .build()));
    }

    private void saveSavingsGoals(ParsedAssistantResponse parsed) {
        if (parsed == null || parsed.savingsGoals() == null) return;
        parsed.savingsGoals().forEach(s -> savingsGoalRepository.save(SavingsGoal.builder()
                .title(s.title())
                .targetAmount(s.targetAmount())
                .currentAmount(s.currentAmount())
                .targetDate(s.targetDate())
                .status(enumOrDefault(RecordStatus.class, s.status(), RecordStatus.IN_PROGRESS))
                .build()));
    }

    private void saveBudgetCategories(ParsedAssistantResponse parsed) {
        if (parsed == null || parsed.budgetCategories() == null) return;
        parsed.budgetCategories().forEach(b -> budgetCategoryRepository.save(BudgetCategory.builder()
                .category(emptyToEtc(b.category()))
                .monthlyLimit(b.monthlyLimit())
                .alertThresholdPercent(b.alertThresholdPercent())
                .build()));
    }

    private void saveFinanceSummaries(ParsedAssistantResponse parsed) {
        if (parsed == null || parsed.financeSummaries() == null) return;
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
                    .intent(parsed == null || parsed.mode() == null ? "ASSISTANT_MESSAGE" : parsed.mode())
                    .parsedResult(parsed == null ? null : objectMapper.writeValueAsString(parsed))
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("AssistantLog 저장 실패", e);
        }
    }

    private String buildSaveSummary(ParsedAssistantResponse p, int estimatedCalories) {
        int f = size(p.finances()), h = size(p.healthRecords()), w = size(p.workouts()), t = size(p.tasks());
        int g = size(p.goals()), pr = size(p.projects()), hb = size(p.habitTrackers()), j = size(p.journals());
        StringBuilder sb = new StringBuilder("저장 완료했어 ✨\n");
        sb.append("가계부 ").append(f).append("개 · 건강 ").append(h + (estimatedCalories > 0 && !hasCalories(p) ? 1 : 0)).append("개 · 운동 ").append(w).append("개 · 할 일 ").append(t).append("개\n");
        sb.append("목표 ").append(g).append("개 · 프로젝트 ").append(pr).append("개 · 습관 ").append(hb).append("개 · 저널 ").append(j).append("개");
        if (estimatedCalories > 0 && !hasCalories(p)) sb.append("\n🍽️ 음식은 평균값으로 약 ").append(estimatedCalories).append("kcal로 추정해서 저장했어.");
        return sb.toString();
    }

    private String buildBudgetWarning() {
        LocalDate now = LocalDate.now(SEOUL);
        LocalDateTime start = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = now.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);
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
        LocalDate today = LocalDate.now(SEOUL);
        Double water = healthRecordRepository.sumValueByTypeAndDate(HealthType.WATER, today);
        return "오늘 물은 총 " + formatNumber(water) + "ml 마셨어 💧\n조금 적다면 다음 알림 때 한 컵만 더 마셔보자.";
    }

    private String answerCaloriesToday() {
        LocalDate today = LocalDate.now(SEOUL);
        Double calories = healthRecordRepository.sumValueByTypeAndDate(HealthType.CALORIES, today);
        List<HealthRecord> records = healthRecordRepository.findByTypeAndRecordedDateBetween(HealthType.CALORIES, today, today);
        StringBuilder sb = new StringBuilder("오늘 섭취 칼로리는 약 ").append(formatNumber(calories)).append("kcal야 🍽️\n");
        if (!records.isEmpty()) {
            sb.append("기록 내역:\n");
            records.stream().limit(8).forEach(r -> sb.append("- ").append(r.getMemo()).append(" · ").append(formatNumber(r.getValue())).append("kcal\n"));
        }
        sb.append("정확한 영양 계산이라기보다는 평균값 기반 추정치야.");
        return sb.toString();
    }

    private String answerMonthlyFinance() {
        LocalDate now = LocalDate.now(SEOUL);
        LocalDateTime start = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = now.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);
        int income = nvl(financeRecordRepository.sumAmountByTypeAndPeriod(FinanceType.INCOME, start, end));
        int expense = nvl(financeRecordRepository.sumAmountByTypeAndPeriod(FinanceType.EXPENSE, start, end));
        int saving = nvl(financeRecordRepository.sumAmountByTypeAndPeriod(FinanceType.SAVING, start, end));
        String warning = buildBudgetWarning();
        return "이번 달 정리야 💰\n수입: " + income + "원\n지출: " + expense + "원\n저축: " + saving + "원\n남은 흐름: " + (income - expense - saving) + "원" +
                (warning.isBlank() ? "" : "\n\n" + warning);
    }

    private String answerGoals() {
        List<Goal> goals = goalRepository.findByStatus(RecordStatus.IN_PROGRESS);
        if (goals.isEmpty()) return "진행 중인 목표가 아직 없어. 목표를 말해주면 바로 저장하고 계획을 세워줄게.";
        StringBuilder sb = new StringBuilder("진행 중인 목표야 🎯\n");
        goals.stream().limit(10).forEach(g -> sb.append("- ").append(g.getTitle()).append(" (").append(g.getProgress()).append("%)\n"));
        sb.append("\n원하면 각 목표를 월별 마일스톤으로 쪼개줄 수 있어.");
        return sb.toString();
    }

    private String answerTasks() {
        List<Task> tasks = taskRepository.findByStatus(RecordStatus.TODO);
        if (tasks.isEmpty()) return "남은 할 일이 없어 ✨";
        StringBuilder sb = new StringBuilder("남은 할 일이야 ✅\n");
        tasks.stream().limit(10).forEach(t -> sb.append("☐ ").append(t.getTitle()).append(t.getDueAt() == null ? "" : " · " + t.getDueAt()).append("\n"));
        return sb.toString();
    }

    private boolean isWaterQuestion(String m) { return m.contains("물") && (m.contains("얼마") || m.contains("얼마나") || m.contains("몇") || m.contains("마셨")); }
    private boolean isCalorieQuestion(String m) { return (m.contains("칼로리") || m.contains("kcal")) && (m.contains("얼마") || m.contains("몇") || m.contains("먹었") || m.contains("섭취")); }
    private boolean isFinanceQuestion(String m) { return (m.contains("이번 달") || m.contains("이번달") || m.contains("가계부")) && (m.contains("얼마") || m.contains("지출") || m.contains("수입") || m.contains("돈")); }
    private boolean isGoalQuestion(String m) { return m.contains("목표") && (m.contains("알려") || m.contains("뭐") || m.contains("진행")); }
    private boolean isTaskQuestion(String m) { return (m.contains("할일") || m.contains("할 일") || m.contains("태스크")) && (m.contains("뭐") || m.contains("알려") || m.contains("남")); }
    private boolean isScrumSettingQuestion(String m) { return (m.contains("스크럼") || m.contains("회고") || m.contains("알림")) && (m.contains("몇시") || m.contains("몇 시") || m.contains("설정") || m.contains("언제")); }
    private boolean isCalendarQuestion(String m) { return (m.contains("캘린더") || m.contains("일정")) && (m.contains("뭐") || m.contains("알려") || m.contains("읽") || m.contains("오늘") || m.contains("내일")); }

    private int size(List<?> list) { return list == null ? 0 : list.size(); }
    private int nvl(Integer value) { return value == null ? 0 : value; }
    private String formatNumber(Double value) { return String.valueOf(value == null ? 0 : Math.round(value)); }
    private String emptyToEtc(String value) { return value == null || value.isBlank() ? "ETC" : value; }
    private String enabled(Boolean value) { return Boolean.TRUE.equals(value) ? "ON" : "OFF"; }
    private String formatTime(LocalDateTime dateTime) { return dateTime == null ? "종일" : dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")); }
    private ZoneId zone(String timezone) { try { return ZoneId.of(timezone == null || timezone.isBlank() ? "Asia/Seoul" : timezone); } catch (Exception e) { return SEOUL; } }

    private <E extends Enum<E>> E enumOrDefault(Class<E> type, String value, E defaultValue) {
        try { return value == null ? defaultValue : Enum.valueOf(type, value.toUpperCase(Locale.ROOT)); }
        catch (Exception e) { return defaultValue; }
    }
}
