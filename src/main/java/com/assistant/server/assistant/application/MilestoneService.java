package com.assistant.server.assistant.application;

import com.assistant.server.assistant.domain.entity.Milestone;
import com.assistant.server.assistant.domain.repository.MilestoneRepository;
import com.assistant.server.assistant.presentation.dto.request.MilestoneCreateRequest;
import com.assistant.server.assistant.presentation.dto.response.MilestoneResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;

    @Transactional
    public Long create(MilestoneCreateRequest request) {
        Milestone milestone = Milestone.builder()
                .goalId(request.goalId())
                .title(request.title())
                .dueDate(request.dueDate())
                .completed(request.completed())
                .build();

        return milestoneRepository.save(milestone).getId();
    }

    @Transactional(readOnly = true)
    public List<MilestoneResponse> getAll() {
        return milestoneRepository.findAll()
                .stream()
                .map(MilestoneResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MilestoneResponse getById(Long id) {
        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Milestone not found"));

        return MilestoneResponse.from(milestone);
    }

    @Transactional
    public void delete(Long id) {
        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Milestone not found"));

        milestoneRepository.delete(milestone);
    }
}