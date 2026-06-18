package com.assistant.server.assistant.application;

import com.assistant.server.assistant.domain.entity.HealthRecord;
import com.assistant.server.assistant.domain.repository.HealthRecordRepository;
import com.assistant.server.assistant.presentation.dto.request.HealthRecordCreateRequest;
import com.assistant.server.assistant.presentation.dto.request.HealthRecordUpdateRequest;
import com.assistant.server.assistant.presentation.dto.response.HealthRecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthRecordService {

    private final HealthRecordRepository healthRecordRepository;

    @Transactional
    public Long create(HealthRecordCreateRequest request) {
        HealthRecord healthRecord = HealthRecord.builder()
                .type(request.type())
                .value(request.value())
                .unit(request.unit())
                .memo(request.memo())
                .recordedDate(request.recordedDate())
                .build();

        return healthRecordRepository.save(healthRecord).getId();
    }

    @Transactional(readOnly = true)
    public List<HealthRecordResponse> getAll() {
        return healthRecordRepository.findAll()
                .stream()
                .map(HealthRecordResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public HealthRecordResponse getById(Long id) {
        HealthRecord record = healthRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Health record not found"));

        return HealthRecordResponse.from(record);
    }

    @Transactional
    public Long update(Long id, HealthRecordUpdateRequest request) {
        HealthRecord record = healthRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Health record not found"));

        record.update(
                request.type(),
                request.value(),
                request.unit(),
                request.memo(),
                request.recordedDate()
        );

        return record.getId();
    }

    @Transactional
    public void delete(Long id) {
        HealthRecord record = healthRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Health record not found"));

        healthRecordRepository.delete(record);
    }
}