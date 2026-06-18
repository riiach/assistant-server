package com.assistant.server.assistant.application;

import com.assistant.server.assistant.domain.entity.*;
import com.assistant.server.assistant.domain.enums.FinanceType;
import com.assistant.server.assistant.domain.enums.HealthType;
import com.assistant.server.assistant.domain.repository.*;
import com.assistant.server.gemini.dto.ParsedAssistantResponse;
import com.assistant.server.gemini.infrastructure.GeminiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public String handleMessage(String message) {
        ParsedAssistantResponse parsed = geminiService.parseMessageToDto(message);

        saveFinances(parsed);
        saveHealthRecords(parsed);
        saveWorkouts(parsed);
        saveTasks(parsed);
        saveLog(message, parsed);

        return "저장 완료";
    }

    private void saveFinances(ParsedAssistantResponse parsed) {
        if (parsed.finances() == null) return;

        parsed.finances().forEach(finance ->
                financeRecordRepository.save(
                        FinanceRecord.builder()
                                .type(FinanceType.valueOf(finance.type()))
                                .amount(finance.amount())
                                .category(finance.category())
                                .memo(finance.memo())
                                .build()
                )
        );
    }

    private void saveHealthRecords(ParsedAssistantResponse parsed) {
        if (parsed.healthRecords() == null) return;

        parsed.healthRecords().forEach(health ->
                healthRecordRepository.save(
                        HealthRecord.builder()
                                .type(HealthType.valueOf(health.type()))
                                .value(health.value())
                                .unit(health.unit())
                                .memo(health.memo())
                                .build()
                )
        );
    }

    private void saveWorkouts(ParsedAssistantResponse parsed) {
        if (parsed.workouts() == null) return;

        parsed.workouts().forEach(workout ->
                workoutRecordRepository.save(
                        WorkoutRecord.builder()
                                .title(workout.title())
                                .durationMinutes(workout.durationMinutes())
                                .caloriesBurned(workout.caloriesBurned())
                                .memo(workout.memo())
                                .build()
                )
        );
    }

    private void saveTasks(ParsedAssistantResponse parsed) {
        if (parsed.tasks() == null) return;

        parsed.tasks().forEach(task ->
                taskRepository.save(
                        Task.builder()
                                .title(task.title())
                                .memo(task.memo())
                                .build()
                )
        );
    }

    private void saveLog(String message, ParsedAssistantResponse parsed) {
        try {
            assistantLogRepository.save(
                    AssistantLog.builder()
                            .rawMessage(message)
                            .intent("GEMINI_PARSED_MESSAGE")
                            .parsedResult(objectMapper.writeValueAsString(parsed))
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("AssistantLog 저장 실패", e);
        }
    }
}