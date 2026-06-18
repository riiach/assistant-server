package com.assistant.server.assistant.application;

import com.assistant.server.assistant.domain.entity.Goal;
import com.assistant.server.assistant.domain.repository.GoalRepository;
import com.assistant.server.assistant.presentation.dto.request.GoalCreateRequest;
import com.assistant.server.assistant.presentation.dto.response.GoalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;

    @Transactional
    public Long create(GoalCreateRequest request) {
        Goal goal = Goal.builder()
                .title(request.title())
                .description(request.description())
                .type(request.type())
                .targetYear(request.targetYear())
                .targetMonth(request.targetMonth())
                .targetDate(request.targetDate())
                .progress(request.progress())
                .status(request.status())
                .build();

        return goalRepository.save(goal).getId();
    }

    @Transactional(readOnly = true)
    public List<GoalResponse> getAll() {
        return goalRepository.findAll()
                .stream()
                .map(GoalResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public GoalResponse getById(Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        return GoalResponse.from(goal);
    }

    @Transactional
    public void delete(Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        goalRepository.delete(goal);
    }
}