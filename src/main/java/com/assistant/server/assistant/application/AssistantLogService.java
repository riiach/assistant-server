package com.assistant.server.assistant.application;

import com.assistant.server.assistant.domain.entity.AssistantLog;
import com.assistant.server.assistant.domain.repository.AssistantLogRepository;
import com.assistant.server.assistant.presentation.dto.request.AssistantLogCreateRequest;
import com.assistant.server.assistant.presentation.dto.response.AssistantLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssistantLogService {

    private final AssistantLogRepository assistantLogRepository;

    @Transactional
    public Long create(AssistantLogCreateRequest request) {
        AssistantLog log = AssistantLog.builder()
                .rawMessage(request.rawMessage())
                .intent(request.intent())
                .parsedResult(request.parsedResult())
                .build();

        return assistantLogRepository.save(log).getId();
    }

    @Transactional(readOnly = true)
    public List<AssistantLogResponse> getAll() {
        return assistantLogRepository.findAll()
                .stream()
                .map(AssistantLogResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AssistantLogResponse getById(Long id) {
        AssistantLog log = assistantLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assistant log not found"));

        return AssistantLogResponse.from(log);
    }

    @Transactional
    public void delete(Long id) {
        AssistantLog log = assistantLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assistant log not found"));

        assistantLogRepository.delete(log);
    }
}