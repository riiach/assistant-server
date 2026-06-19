package com.assistant.server.assistant.domain.repository;

import com.assistant.server.assistant.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {}
