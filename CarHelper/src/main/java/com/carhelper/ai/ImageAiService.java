package com.carhelper.ai;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageAiService {

    private final GeminiAiService geminiAiService;

    public ImageAiService(GeminiAiService geminiAiService) {
        this.geminiAiService = geminiAiService;
    }

    public String analyzeImage(MultipartFile file) {
        return analyzeImage(file, "en");
    }

    public String analyzeImage(MultipartFile file, String language) {
        return geminiAiService.analyzeImage(file, language);
    }
}