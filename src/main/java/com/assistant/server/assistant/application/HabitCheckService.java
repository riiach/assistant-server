package com.assistant.server.assistant.application;

import com.assistant.server.assistant.domain.entity.HabitCheck;
import com.assistant.server.assistant.domain.repository.HabitCheckRepository;
import com.assistant.server.assistant.presentation.dto.request.HabitCheckCreateRequest;
import com.assistant.server.assistant.presentation.dto.request.HabitCheckUpdateRequest;
import com.assistant.server.assistant.presentation.dto.response.HabitCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitCheckService {

    private final HabitCheckRepository habitCheckRepository;

    @Transactional
    public Long create(HabitCheckCreateRequest request) {
        HabitCheck habitCheck = HabitCheck.builder()
                .habitTrackerId(request.habitTrackerId())
                .checkedDate(request.checkedDate())
                .completed(request.completed())
                .build();

        return habitCheckRepository.save(habitCheck).getId();
    }

    @Transactional(readOnly = true)
    public List<HabitCheckResponse> getAll() {
        return habitCheckRepository.findAll()
                .stream()
                .map(HabitCheckResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public HabitCheckResponse getById(Long id) {
        HabitCheck habitCheck = habitCheckRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Habit check not found"));

        return HabitCheckResponse.from(habitCheck);
    }

    @Transactional
    public Long update(Long id, HabitCheckUpdateRequest request) {
        HabitCheck habitCheck = habitCheckRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Habit check not found"));

        habitCheck.update(
                request.habitTrackerId(),
                request.checkedDate(),
                request.completed()
        );

        return habitCheck.getId();
    }

    @Transactional
    public void delete(Long id) {
        HabitCheck habitCheck = habitCheckRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Habit check not found"));

        habitCheckRepository.delete(habitCheck);
    }
}