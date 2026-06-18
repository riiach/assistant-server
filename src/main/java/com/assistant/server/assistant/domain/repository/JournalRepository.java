package com.assistant.server.assistant.domain.repository;

import com.assistant.server.assistant.domain.entity.Journal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JournalRepository extends JpaRepository<Journal, Long> {

    List<Journal> findByJournalDate(LocalDate journalDate);

    List<Journal> findByJournalDateBetween(LocalDate startDate, LocalDate endDate);

    List<Journal> findByMood(String mood);
}