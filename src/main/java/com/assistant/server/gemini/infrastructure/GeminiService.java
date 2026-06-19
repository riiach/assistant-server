package com.assistant.server.gemini.infrastructure;

import com.assistant.server.gemini.dto.ParsedAssistantResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public String parseMessage(String message) {
        String prompt = """
                너는 리아의 개인 AI 비서 앱을 위한 데이터 분류기이자 계획 어시스턴트다.
                사용자의 한국어 자연어 문장을 분석해서 반드시 JSON만 반환해.
                코드블록, 설명, 마크다운을 절대 붙이지 마.

                mode는 다음 중 하나로 정해:
                - RECORD: 기록/저장해야 하는 말
                - QUERY: DB에서 조회해서 답해야 하는 질문
                - PLAN: 목표/계획을 세워야 하는 말

                반환 JSON 구조는 반드시 아래 키를 모두 포함해. 해당 없으면 [] 또는 null.
                {
                  "mode": "RECORD | QUERY | PLAN",
                  "answer": "질문이면 간단한 의도 설명, 기록이면 null",
                  "finances": [{"type":"EXPENSE | INCOME | SAVING", "amount":0, "category":"CAFE | FOOD | TRANSPORT | SHOPPING | SALARY | ETC", "memo":"내용"}],
                  "healthRecords": [{"type":"WEIGHT | CALORIES | WATER | SLEEP | MOOD | PERIOD", "value":0, "unit":"kg | kcal | ml | hour | day | text", "memo":"내용"}],
                  "workouts": [{"title":"운동명", "durationMinutes":0, "caloriesBurned":0, "memo":"내용"}],
                  "tasks": [{"projectId":null, "title":"할 일", "memo":"내용", "dueAt":null, "status":"TODO | IN_PROGRESS | DONE"}],
                  "goals": [{"title":"목표명", "description":"상세 설명과 AI 계획", "type":"HEALTH | CAREER | FINANCE | PROJECT | ETC", "targetYear":2026, "targetMonth":6, "targetDate":null, "progress":0, "status":"IN_PROGRESS"}],
                  "milestones": [{"goalId":null, "title":"마일스톤", "dueDate":null, "completed":false}],
                  "projects": [{"name":"프로젝트명", "description":"설명", "priority":"HIGH | MEDIUM | LOW", "startDate":null, "targetDate":null, "status":"IN_PROGRESS"}],
                  "habitTrackers": [{"title":"습관명", "description":"설명", "status":"IN_PROGRESS"}],
                  "journals": [{"mood":"감정", "content":"일기 내용", "journalDate":null}],
                  "savingsGoals": [{"title":"저축 목표", "targetAmount":0, "currentAmount":0, "targetDate":null, "status":"IN_PROGRESS"}],
                  "budgetCategories": [{"category":"CAFE | FOOD | TRANSPORT | SHOPPING | ETC", "monthlyLimit":0, "alertThresholdPercent":90}],
                  "financeSummaries": [{"targetYear":2026, "targetMonth":6, "monthlyBudget":0, "monthlyIncomeTarget":0, "monthlySavingTarget":0}],
                  "calendarEvents": [{"title":"일정 제목", "startAt":null, "endAt":null, "description":"설명"}]
                }

                예: '오늘 물 500ml 마셨어'는 healthRecords에 WATER로 저장.
                예: '오늘 물 얼마나 마셨더라?'는 mode QUERY, 배열은 비워둠.
                예: '다음달까지 10kg 빼는 게 목표야'는 goals, habitTrackers, tasks, milestones까지 생성.

                사용자 문장:
                %s
                """.formatted(message);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
        );

        String rawResponse = webClientBuilder.build()
                .post()
                .uri(apiUrl + "?key=" + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return extractJsonText(rawResponse);
    }

    public ParsedAssistantResponse parseMessageToDto(String message) {
        String json = parseMessage(message);
        try {
            return objectMapper.readValue(json, ParsedAssistantResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Gemini JSON → DTO 변환 실패. raw json=" + json, e);
        }
    }

    @SuppressWarnings("unchecked")
    private String extractJsonText(String rawResponse) {
        try {
            Map<String, Object> responseMap = objectMapper.readValue(rawResponse, Map.class);
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseMap.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String text = (String) parts.get(0).get("text");
            return text.replace("```json", "").replace("```", "").trim();
        } catch (Exception e) {
            throw new RuntimeException("Gemini 응답 파싱 실패", e);
        }
    }
}
