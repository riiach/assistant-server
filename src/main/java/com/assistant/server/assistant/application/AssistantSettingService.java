package com.assistant.server.assistant.application;

import com.assistant.server.assistant.domain.entity.AssistantSetting;
import com.assistant.server.assistant.domain.repository.AssistantSettingRepository;
import com.assistant.server.assistant.presentation.dto.request.AssistantSettingUpdateRequest;
import com.assistant.server.assistant.presentation.dto.response.AssistantSettingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssistantSettingService {

    private final AssistantSettingRepository assistantSettingRepository;

    @Transactional
    public AssistantSetting getOrCreate() {
        return assistantSettingRepository.findAll().stream().findFirst()
                .orElseGet(() -> assistantSettingRepository.save(AssistantSetting.builder().build()));
    }

    @Transactional
    public AssistantSettingResponse get() {
        AssistantSetting setting = getOrCreate();
        return AssistantSettingResponse.from(setting);
    }

    @Transactional
    public AssistantSettingResponse update(AssistantSettingUpdateRequest request) {
        AssistantSetting setting = getOrCreate();
        setting.update(
                request.morningScrumTime(),
                request.eveningReviewTime(),
                request.morningScrumEnabled(),
                request.eveningReviewEnabled(),
                request.calendarReadEnabled(),
                request.calendarReminderEnabled(),
                request.eventReminderMinutes(),
                request.timezone(),
                request.slackChannelId()
        );
        return AssistantSettingResponse.from(setting);
    }
}
