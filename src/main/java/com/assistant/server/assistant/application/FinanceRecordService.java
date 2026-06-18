package com.assistant.server.assistant.application;

import com.assistant.server.assistant.domain.entity.FinanceRecord;
import com.assistant.server.assistant.domain.repository.FinanceRecordRepository;
import com.assistant.server.assistant.presentation.dto.request.FinanceRecordCreateRequest;
import com.assistant.server.assistant.presentation.dto.request.FinanceRecordUpdateRequest;
import com.assistant.server.assistant.presentation.dto.response.FinanceRecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceRecordService {

    private final FinanceRecordRepository financeRecordRepository;

    @Transactional
    public Long create(FinanceRecordCreateRequest request) {
        FinanceRecord record = FinanceRecord.builder()
                .type(request.type())
                .amount(request.amount())
                .category(request.category())
                .memo(request.memo())
                .occurredAt(request.occurredAt())
                .build();

        return financeRecordRepository.save(record).getId();
    }

    @Transactional(readOnly = true)
    public List<FinanceRecordResponse> getAll() {
        return financeRecordRepository.findAll()
                .stream()
                .map(FinanceRecordResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public FinanceRecordResponse getById(Long id) {
        FinanceRecord record = financeRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Finance record not found"));

        return FinanceRecordResponse.from(record);
    }

    @Transactional
    public Long update(Long id, FinanceRecordUpdateRequest request) {
        FinanceRecord record = financeRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Finance record not found"));

        record.update(
                request.type(),
                request.amount(),
                request.category(),
                request.memo(),
                request.occurredAt()
        );

        return record.getId();
    }

    @Transactional
    public void delete(Long id) {
        FinanceRecord record = financeRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Finance record not found"));

        financeRecordRepository.delete(record);
    }
}