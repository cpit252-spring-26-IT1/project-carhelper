package com.carhelper.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class GeminiAiService implements AiService {

    @Value("${ai.api.key:}")
    private String apiKey;

    @Value("${ai.model:gemini-2.5-flash}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String analyzeText(String prompt) {
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("YOUR_GEMINI_API_KEY")) {
            return "Gemini API key is missing. Add your key in application.properties using ai.api.key.";
        }

        try {
            Map<String, Object> textPart = Map.of("text", prompt);
            Map<String, Object> content = Map.of("parts", List.of(textPart));
            Map<String, Object> requestBody = Map.of("contents", List.of(content));
            HttpEntity<String> entity = buildEntity(requestBody);
            String response = restTemplate.postForObject(buildUrl(), entity, String.class);
            return extractText(response);
        } catch (Exception e) {
            return "AI analysis failed: " + e.getMessage();
        }
    }

    @Override
    public String analyzeImage(MultipartFile file, String language) {
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("YOUR_GEMINI_API_KEY")) {
            return "Gemini API key is missing. Add your key in application.properties using ai.api.key.";
        }

        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            String reportLanguage = "Arabic".equalsIgnoreCase(language) || "ar".equalsIgnoreCase(language) ? "Arabic" : "English";
            String prompt = "You are a car image diagnosis assistant. Analyze this car image. It may show car damage, dashboard warning light, or a mechanical issue. Write the full report in " + reportLanguage + ". Return only valid JSON with these exact fields: issueName, detectedProblems, repairSuggestion, estimatedCost, aiDisclaimer. Do not use markdown. Use Saudi Riyal for cost estimates.";

            Map<String, Object> textPart = Map.of("text", prompt);
            Map<String, Object> imageData = Map.of("mimeType", file.getContentType(), "data", base64Image);
            Map<String, Object> imagePart = Map.of("inlineData", imageData);
            Map<String, Object> content = Map.of("parts", List.of(textPart, imagePart));
            Map<String, Object> requestBody = Map.of("contents", List.of(content));
            HttpEntity<String> entity = buildEntity(requestBody);
            return restTemplate.postForObject(buildUrl(), entity, String.class);
        } catch (Exception e) {
            return "AI image analysis failed: " + e.getMessage();
        }
    }

    private HttpEntity<String> buildEntity(Map<String, Object> requestBody) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(mapper.writeValueAsString(requestBody), headers);
    }

    private String buildUrl() {
        return "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;
    }

    private String extractText(String response) throws Exception {
        JsonNode root = mapper.readTree(response);
        return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
    }

    private String cleanErrorMessage(String message) {
        if (message == null) {
            return "AI service error. Please try again.";
        }

        String lower = message.toLowerCase();

        if (message.contains("404") || lower.contains("not found") || lower.contains("model")) {
            return "AI model is not available. Please check ai.model in application.properties.";
        }

        if (message.contains("401") || message.contains("403") || lower.contains("api key")) {
            return "Gemini API key is missing or invalid. Please check application.properties.";
        }

        if (message.contains("429") || lower.contains("quota") || lower.contains("rate limit")) {
            return "AI request limit reached. Please try again later.";
        }

        if (message.contains("503") || lower.contains("unavailable") || lower.contains("high demand")) {
            return "AI service is busy now. Please try again in a few minutes.";
        }

        if (lower.contains("timeout")) {
            return "AI service took too long to respond. Please try again.";
        }

        return "AI analysis failed. Please try again.";
    }
}