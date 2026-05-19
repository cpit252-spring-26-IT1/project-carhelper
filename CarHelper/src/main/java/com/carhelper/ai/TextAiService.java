package com.carhelper.ai;

import org.springframework.stereotype.Service;

@Service
public class TextAiService {

    private final GroqAiService groqAiService;
    private final DeepSeekAiService deepSeekAiService;
    private final GeminiAiService geminiAiService;

    public TextAiService(GroqAiService groqAiService, DeepSeekAiService deepSeekAiService, GeminiAiService geminiAiService) {
        this.groqAiService = groqAiService;
        this.deepSeekAiService = deepSeekAiService;
        this.geminiAiService = geminiAiService;
    }

    public String analyzeText(String prompt) {
        return analyzeText(prompt, null);
    }

    public String analyzeText(String prompt, String language) {
        if (isArabic(prompt, language)) {
            return geminiAiService.analyzeText(prompt);
        }

        String groqResult = groqAiService.analyzeText(prompt);

        if (!shouldFallback(groqResult)) {
            return groqResult;
        }

        String deepSeekResult = deepSeekAiService.analyzeText(prompt);

        if (!shouldFallback(deepSeekResult)) {
            return deepSeekResult;
        }

        return geminiAiService.analyzeText(prompt);
    }

    private boolean isArabic(String prompt, String language) {
        if ("ar".equalsIgnoreCase(language)) {
            return true;
        }

        if (prompt == null) {
            return false;
        }

        String lowerPrompt = prompt.toLowerCase();

        return lowerPrompt.contains("arabic")
                || lowerPrompt.contains("العربية")
                || lowerPrompt.contains("اكتب")
                || lowerPrompt.contains("السيارة")
                || lowerPrompt.matches(".*[\\u0600-\\u06FF].*");
    }

    private boolean shouldFallback(String result) {
        if (result == null || result.isBlank()) {
            return true;
        }

        String text = result.toLowerCase();

        return text.contains("token")
                || text.contains("quota")
                || text.contains("rate limit")
                || text.contains("limit reached")
                || text.contains("request limit")
                || text.contains("429")
                || text.contains("401")
                || text.contains("403")
                || text.contains("api key")
                || text.contains("unauthorized")
                || text.contains("unavailable")
                || text.contains("busy")
                || text.contains("failed")
                || text.contains("empty response");
    }
}