package com.assistant.server.calendar.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@RequiredArgsConstructor
public class GoogleCalendarClient {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${google.calendar.enabled:false}")
    private boolean enabled;

    @Value("${google.calendar.id:primary}")
    private String calendarId;

    @Value("${google.calendar.client-email:}")
    private String clientEmail;

    @Value("${google.calendar.private-key:}")
    private String privateKey;

    public boolean isConfigured() {
        return enabled && notBlank(calendarId) && notBlank(clientEmail) && notBlank(privateKey);
    }

    public List<CalendarEventSummary> getEvents(LocalDateTime start, LocalDateTime end, ZoneId zoneId) {
        if (!isConfigured()) return List.of();
        try {
            String accessToken = requestAccessToken();
            String timeMin = start.atZone(zoneId).toInstant().toString();
            String timeMax = end.atZone(zoneId).toInstant().toString();

            String response = webClientBuilder.build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("www.googleapis.com")
                            .path("/calendar/v3/calendars/{calendarId}/events")
                            .queryParam("timeMin", timeMin)
                            .queryParam("timeMax", timeMax)
                            .queryParam("singleEvents", true)
                            .queryParam("orderBy", "startTime")
                            .build(calendarId))
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseEvents(response, zoneId);
        } catch (Exception e) {
            throw new RuntimeException("Google Calendar 조회 실패: " + e.getMessage(), e);
        }
    }

    public List<CalendarEventSummary> getTodayEvents(ZoneId zoneId) {
        LocalDate today = LocalDate.now(zoneId);
        return getEvents(today.atStartOfDay(), today.plusDays(1).atStartOfDay(), zoneId);
    }

    public List<CalendarEventSummary> getUpcomingEvents(LocalDateTime from, LocalDateTime to, ZoneId zoneId) {
        return getEvents(from, to, zoneId);
    }

    public String createEvent(String title, LocalDateTime startAt, LocalDateTime endAt, String description) {
        return "Google Calendar 쓰기 기능은 현재 비활성화되어 있어. 읽기 기능만 사용해.";
    }

    private String requestAccessToken() throws Exception {
        String assertion = createServiceAccountJwt();
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
        form.add("assertion", assertion);

        String response = webClientBuilder.build()
                .post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JsonNode root = objectMapper.readTree(response);
        JsonNode token = root.get("access_token");
        if (token == null || token.asText().isBlank()) {
            throw new IllegalStateException("Google access_token을 받을 수 없어: " + response);
        }
        return token.asText();
    }

    private String createServiceAccountJwt() throws Exception {
        long now = Instant.now().getEpochSecond();
        Map<String, Object> header = Map.of("alg", "RS256", "typ", "JWT");
        Map<String, Object> claims = Map.of(
                "iss", clientEmail,
                "scope", "https://www.googleapis.com/auth/calendar.readonly",
                "aud", "https://oauth2.googleapis.com/token",
                "iat", now,
                "exp", now + 3600
        );

        String encodedHeader = base64Url(objectMapper.writeValueAsBytes(header));
        String encodedClaims = base64Url(objectMapper.writeValueAsBytes(claims));
        String unsigned = encodedHeader + "." + encodedClaims;

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(loadPrivateKey(normalizePrivateKey(privateKey)));
        signature.update(unsigned.getBytes(StandardCharsets.UTF_8));
        return unsigned + "." + base64Url(signature.sign());
    }

    private PrivateKey loadPrivateKey(String pem) throws Exception {
        String cleaned = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(cleaned);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    private List<CalendarEventSummary> parseEvents(String response, ZoneId zoneId) throws Exception {
        JsonNode items = objectMapper.readTree(response).get("items");
        if (items == null || !items.isArray()) return List.of();
        List<CalendarEventSummary> result = new ArrayList<>();
        for (JsonNode item : items) {
            result.add(new CalendarEventSummary(
                    text(item, "id"),
                    text(item, "summary", "제목 없음"),
                    parseEventDateTime(item.get("start"), zoneId),
                    parseEventDateTime(item.get("end"), zoneId),
                    text(item, "description"),
                    text(item, "location")
            ));
        }
        return result;
    }

    private LocalDateTime parseEventDateTime(JsonNode node, ZoneId zoneId) {
        if (node == null) return null;
        if (node.hasNonNull("dateTime")) return OffsetDateTime.parse(node.get("dateTime").asText()).atZoneSameInstant(zoneId).toLocalDateTime();
        if (node.hasNonNull("date")) return LocalDate.parse(node.get("date").asText(), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        return null;
    }

    private String text(JsonNode node, String field) { return text(node, field, null); }
    private String text(JsonNode node, String field, String defaultValue) {
        JsonNode value = node == null ? null : node.get(field);
        return value == null || value.isNull() ? defaultValue : value.asText();
    }

    private String normalizePrivateKey(String key) { return key == null ? "" : key.replace("\\n", "\n"); }
    private String base64Url(byte[] bytes) { return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes); }
    private boolean notBlank(String value) { return value != null && !value.isBlank(); }
}
