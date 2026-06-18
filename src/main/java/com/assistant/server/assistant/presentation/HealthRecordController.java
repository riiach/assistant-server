package com.assistant.server.assistant.presentation;

import com.assistant.server.assistant.application.HealthRecordService;
import com.assistant.server.assistant.presentation.dto.request.HealthRecordCreateRequest;
import com.assistant.server.assistant.presentation.dto.request.HealthRecordUpdateRequest;
import com.assistant.server.assistant.presentation.dto.response.HealthRecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/health-records")
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    @PostMapping
    public Long create(@RequestBody HealthRecordCreateRequest request) {
        return healthRecordService.create(request);
    }

    @GetMapping
    public List<HealthRecordResponse> getAll() {
        return healthRecordService.getAll();
    }

    @GetMapping("/{id}")
    public HealthRecordResponse getById(@PathVariable Long id) {
        return healthRecordService.getById(id);
    }

    @PutMapping("/{id}")
    public Long update(
            @PathVariable Long id,
            @RequestBody HealthRecordUpdateRequest request
    ) {
        return healthRecordService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        healthRecordService.delete(id);
    }
}