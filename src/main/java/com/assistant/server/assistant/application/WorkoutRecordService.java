package com.assistant.server.assistant.application;

import com.assistant.server.assistant.domain.entity.WorkoutRecord;
import com.assistant.server.assistant.domain.repository.WorkoutRecordRepository;
import com.assistant.server.assistant.presentation.dto.request.WorkoutRecordCreateRequest;
import com.assistant.server.assistant.presentation.dto.response.WorkoutRecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutRecordService {

    private final WorkoutRecordRepository workoutRecordRepository;

    @Transactional
    public Long create(WorkoutRecordCreateRequest request) {
        WorkoutRecord workoutRecord = WorkoutRecord.builder()
                .title(request.title())
                .durationMinutes(request.durationMinutes())
                .caloriesBurned(request.caloriesBurned())
                .memo(request.memo())
                .performedAt(request.performedAt())
                .build();

        return workoutRecordRepository.save(workoutRecord).getId();
    }

    @Transactional(readOnly = true)
    public List<WorkoutRecordResponse> getAll() {
        return workoutRecordRepository.findAll()
                .stream()
                .map(WorkoutRecordResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkoutRecordResponse getById(Long id) {
        WorkoutRecord record = workoutRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workout record not found"));

        return WorkoutRecordResponse.from(record);
    }

    @Transactional
    public void delete(Long id) {
        WorkoutRecord record = workoutRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workout record not found"));

        workoutRecordRepository.delete(record);
    }
}