package com.assistant.server.assistant.presentation;

import com.assistant.server.assistant.domain.entity.SavingsGoal;
import com.assistant.server.assistant.domain.repository.SavingsGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/savings-goals")
public class SavingsGoalController {
    private final SavingsGoalRepository savingsGoalRepository;
    @PostMapping public Long create(@RequestBody SavingsGoal request) { return savingsGoalRepository.save(request).getId(); }
    @GetMapping public List<SavingsGoal> getAll() { return savingsGoalRepository.findAll(); }
    @GetMapping("/{id}") public SavingsGoal getById(@PathVariable Long id) { return savingsGoalRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Savings goal not found")); }
    @PutMapping("/{id}") public Long update(@PathVariable Long id, @RequestBody SavingsGoal request) {
        SavingsGoal goal = savingsGoalRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Savings goal not found"));
        goal.update(request.getTitle(), request.getTargetAmount(), request.getCurrentAmount(), request.getTargetDate(), request.getStatus());
        return savingsGoalRepository.save(goal).getId();
    }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { savingsGoalRepository.deleteById(id); }
}
