package com.carhelper.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
public class DeepSeekAiService {

    @Value("${deepseek.api.key:}")
    private String apiKey;

    @Value("${deepseek.model:deepseek-chat}")
    private String model;

    private final ObjectMapper mapper = new ObjectMapper();

    public String analyzeText(String prompt) {
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("YOUR_DEEPSEEK_API_KEY")) {
            return "DeepSeek API key is missing or invalid. Please check application.properties.";
        }

        try {
            Map<String, Object> message = Map.of(
                    "role", "user",
                    "content", prompt
            );

            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "messages", List.of(message)
            );

            String jsonBody = mapper.writeValueAsString(requestBody);
            HttpRequest request = buildRequest(jsonBody);
            HttpResponse<String> response = sendRequest(request);

            if (response.statusCode() != 200) {
                return cleanErrorMessage(response.statusCode(), response.body());
            }

            return extractText(response.body());

        } catch (Exception e) {
            return cleanExceptionMessage(e);
        }
    }

    protected HttpRequest buildRequest(String jsonBody) {
        return HttpRequest.newBuilder()
                .uri(URI.create("https://api.deepseek.com/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
    }

    protected HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    protected String extractText(String responseBody) throws Exception {
        JsonNode root = mapper.readTree(responseBody);
        JsonNode text = root.path("choices").path(0).path("message").path("content");

        if (text.isMissingNode() || text.asText().isBlank()) {
            return "DeepSeek returned an empty response.";
        }

        return text.asText();
    }

    public String cleanErrorMessage(int statusCode, String body) {
        String text = body == null ? "" : body.toLowerCase();

        if (statusCode == 401 || statusCode == 403 || text.contains("api key") || text.contains("unauthorized")) {
            return "DeepSeek API key is missing or invalid. Please check application.properties.";
        }

        if (statusCode == 429 || text.contains("quota") || text.contains("rate limit") || text.contains("token")) {
            return "DeepSeek request limit reached. Please try again later.";
        }

        if (statusCode == 404 || text.contains("model")) {
            return "DeepSeek model is not available. Please check deepseek.model in application.properties.";
        }

        if (statusCode == 503 || text.contains("unavailable") || text.contains("busy")) {
            return "DeepSeek service is busy now. Please try again in a few minutes.";
        }

        return "DeepSeek analysis failed. Please try again.";
    }

    public String cleanExceptionMessage(Exception error) {
        if (error.getMessage() == null) {
            return "DeepSeek analysis failed. Please try again.";
        }

        String message = error.getMessage().toLowerCase();

        if (message.contains("timeout")) {
            return "DeepSeek service took too long to respond. Please try again.";
        }

        if (message.contains("connect")) {
            return "Cannot connect to DeepSeek service. Please try again.";
        }

        return "DeepSeek analysis failed. Please try again.";
    }
}