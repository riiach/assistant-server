package com.assistant.server.slack.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SlackResponseClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${slack.bot.token:test-token}")
    private String botToken;

    public void sendMessage(String channelId, String text) {
        Map<String, Object> body = Map.of("channel", channelId, "text", text);
        webClientBuilder.build()
                .post()
                .uri("https://slack.com/api/chat.postMessage")
                .header("Authorization", "Bearer " + botToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public void sendResponseUrl(String responseUrl, String text) {
        Map<String, Object> body = Map.of(
                "response_type", "ephemeral",
                "replace_original", false,
                "text", text
        );
        webClientBuilder.build()
                .post()
                .uri(responseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
