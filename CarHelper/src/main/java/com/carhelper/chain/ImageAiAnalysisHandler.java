package com.carhelper.chain;

import com.carhelper.adapter.AiResponseAdapter;
import com.carhelper.ai.ImageAiService;
import com.carhelper.model.DiagnosticReport;
import org.springframework.stereotype.Component;

@Component
public class ImageAiAnalysisHandler extends AbstractDiagnosisHandler {
    private final ImageAiService imageAiService;
    private final AiResponseAdapter aiResponseAdapter;

    public ImageAiAnalysisHandler(ImageAiService imageAiService, AiResponseAdapter aiResponseAdapter) {
        this.imageAiService = imageAiService;
        this.aiResponseAdapter = aiResponseAdapter;
    }

    @Override
    public void handle(ImageDiagnosisRequest request) {
        String rawResult = imageAiService.analyzeImage(request.getFile(), request.getLanguage());
        DiagnosticReport report = aiResponseAdapter.translateResponse(rawResult);

        request.setRawAiResponse(rawResult);
        request.setReport(report);

        handleNext(request);
    }
}