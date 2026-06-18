package com.assistant.server.assistant.application;

import com.assistant.server.assistant.domain.entity.HabitTracker;
import com.assistant.server.assistant.domain.repository.HabitTrackerRepository;
import com.assistant.server.assistant.presentation.dto.request.HabitTrackerCreateRequest;
import com.assistant.server.assistant.presentation.dto.response.HabitTrackerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitTrackerService {

    private final HabitTrackerRepository habitTrackerRepository;

    @Transactional
    public Long create(HabitTrackerCreateRequest request) {
        HabitTracker habitTracker = HabitTracker.builder()
                .title(request.title())
                .description(request.description())
                .status(request.status())
                .build();

        return habitTrackerRepository.save(habitTracker).getId();
    }

    @Transactional(readOnly = true)
    public List<HabitTrackerResponse> getAll() {
        return habitTrackerRepository.findAll()
                .stream()
                .map(HabitTrackerResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public HabitTrackerResponse getById(Long id) {
        HabitTracker habitTracker = habitTrackerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Habit tracker not found"));

        return HabitTrackerResponse.from(habitTracker);
    }

    @Transactional
    public void delete(Long id) {
        HabitTracker habitTracker = habitTrackerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Habit tracker not found"));

        habitTrackerRepository.delete(habitTracker);
    }
}