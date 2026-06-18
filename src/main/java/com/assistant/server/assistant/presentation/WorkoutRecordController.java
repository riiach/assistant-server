package com.assistant.server.assistant.presentation;

import com.assistant.server.assistant.application.WorkoutRecordService;
import com.assistant.server.assistant.presentation.dto.request.WorkoutRecordCreateRequest;
import com.assistant.server.assistant.presentation.dto.response.WorkoutRecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workout-records")
public class WorkoutRecordController {

    private final WorkoutRecordService workoutRecordService;

    @PostMapping
    public Long create(@RequestBody WorkoutRecordCreateRequest request) {
        return workoutRecordService.create(request);
    }

    @GetMapping
    public List<WorkoutRecordResponse> getAll() {
        return workoutRecordService.getAll();
    }

    @GetMapping("/{id}")
    public WorkoutRecordResponse getById(@PathVariable Long id) {
        return workoutRecordService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        workoutRecordService.delete(id);
    }
}