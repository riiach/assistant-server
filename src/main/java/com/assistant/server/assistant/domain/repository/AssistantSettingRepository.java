package com.assistant.server.assistant.domain.repository;

import com.assistant.server.assistant.domain.entity.AssistantSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssistantSettingRepository extends JpaRepository<AssistantSetting, Long> {
}
