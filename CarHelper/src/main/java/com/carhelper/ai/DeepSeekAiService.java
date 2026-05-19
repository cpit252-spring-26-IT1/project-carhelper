package com.carhelper.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class DeepSeekAiService {

    @Value("${deepseek.api.key:}")
    private String apiKey;

    @Value("${deepseek.model:deepseek-chat}")
    private String model;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String analyzeText(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            return "DeepSeek API key is missing or invalid. Please check application.properties.";
        }

        try {
            String requestBody = objectMapper.writeValueAsString(new DeepSeekRequest(
                    model,
                    new DeepSeekMessage[]{
                            new DeepSeekMessage("user", prompt)
                    }
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.deepseek.com/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return cleanErrorMessage(response.statusCode(), response.body());
            }

            JsonNode root = objectMapper.readTree(response.body());

            JsonNode content = root
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content");

            if (content.isMissingNode() || content.asText().isBlank()) {
                return "DeepSeek returned an empty response.";
            }

            return content.asText();

        } catch (Exception error) {
            return cleanExceptionMessage(error);
        }
    }

    private String cleanErrorMessage(int statusCode, String body) {
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

    private String cleanExceptionMessage(Exception error) {
        String message = error.getMessage();

        if (message == null) {
            return "DeepSeek analysis failed. Please try again.";
        }

        String text = message.toLowerCase();

        if (text.contains("timeout")) {
            return "DeepSeek service took too long to respond. Please try again.";
        }

        if (text.contains("connect")) {
            return "Cannot connect to DeepSeek service. Please try again.";
        }

        return "DeepSeek analysis failed. Please try again.";
    }

    private record DeepSeekRequest(String model, DeepSeekMessage[] messages) {
    }

    private record DeepSeekMessage(String role, String content) {
    }
}