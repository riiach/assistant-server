package com.assistant.server.assistant.presentation;

import com.assistant.server.assistant.application.FinanceRecordService;
import com.assistant.server.assistant.presentation.dto.request.FinanceRecordCreateRequest;
import com.assistant.server.assistant.presentation.dto.request.FinanceRecordUpdateRequest;
import com.assistant.server.assistant.presentation.dto.response.FinanceRecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/finance-records")
public class FinanceRecordController {

    private final FinanceRecordService financeRecordService;

    @PostMapping
    public Long create(@RequestBody FinanceRecordCreateRequest request) {
        return financeRecordService.create(request);
    }

    @GetMapping
    public List<FinanceRecordResponse> getAll() {
        return financeRecordService.getAll();
    }

    @GetMapping("/{id}")
    public FinanceRecordResponse getById(@PathVariable Long id) {
        return financeRecordService.getById(id);
    }

    @PutMapping("/{id}")
    public Long update(
            @PathVariable Long id,
            @RequestBody FinanceRecordUpdateRequest request
    ) {
        return financeRecordService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        financeRecordService.delete(id);
    }
}