package com.assistant.server.assistant.presentation;

import com.assistant.server.assistant.domain.entity.Journal;
import com.assistant.server.assistant.domain.repository.JournalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/journals")
public class JournalController {
    private final JournalRepository journalRepository;
    @PostMapping public Long create(@RequestBody Journal request) { return journalRepository.save(request).getId(); }
    @GetMapping public List<Journal> getAll() { return journalRepository.findAll(); }
    @GetMapping("/{id}") public Journal getById(@PathVariable Long id) { return journalRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Journal not found")); }
    @PutMapping("/{id}") public Long update(@PathVariable Long id, @RequestBody Journal request) {
        Journal journal = journalRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Journal not found"));
        journal.update(request.getMood(), request.getContent(), request.getJournalDate());
        return journalRepository.save(journal).getId();
    }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { journalRepository.deleteById(id); }
}
