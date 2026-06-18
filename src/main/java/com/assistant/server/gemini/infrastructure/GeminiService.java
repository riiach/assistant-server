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
                너는 개인 비서 앱의 데이터 분류기다.

                사용자의 문장을 분석해서 반드시 JSON만 반환해.
                절대 ```json 같은 코드블록을 붙이지 마.
                설명도 하지 마.

                반환 형식은 반드시 아래 구조를 지켜.

                {
                  "finances": [
                    {
                      "type": "EXPENSE | INCOME | SAVING",
                      "amount": 0,
                      "category": "CAFE | FOOD | TRANSPORT | SHOPPING | SALARY | ETC",
                      "memo": "내용"
                    }
                  ],
                  "healthRecords": [
                    {
                      "type": "WEIGHT | CALORIES | WATER | SLEEP | MOOD | PERIOD",
                      "value": 0,
                      "unit": "kg | kcal | ml | hour | day | text",
                      "memo": "내용"
                    }
                  ],
                  "workouts": [
                    {
                      "title": "운동명",
                      "durationMinutes": 0,
                      "caloriesBurned": 0,
                      "memo": "내용"
                    }
                  ],
                  "tasks": [
                    {
                      "title": "할 일",
                      "memo": "내용"
                    }
                  ]
                }

                해당되는 데이터가 없으면 빈 배열 [] 로 반환해.

                사용자 문장:
                %s
                """.formatted(message);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        String rawResponse = webClientBuilder
                .build()
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
            throw new RuntimeException("Gemini JSON → DTO 변환 실패", e);
        }
    }

    @SuppressWarnings("unchecked")
    private String extractJsonText(String rawResponse) {
        try {
            Map<String, Object> responseMap = objectMapper.readValue(rawResponse, Map.class);

            List<Map<String, Object>> candidates =
                    (List<Map<String, Object>>) responseMap.get("candidates");

            Map<String, Object> content =
                    (Map<String, Object>) candidates.get(0).get("content");

            List<Map<String, Object>> parts =
                    (List<Map<String, Object>>) content.get("parts");

            String text = (String) parts.get(0).get("text");

            return text
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

        } catch (Exception e) {
            throw new RuntimeException("Gemini 응답 파싱 실패", e);
        }
    }
}