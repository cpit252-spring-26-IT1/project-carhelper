package com.carhelper.chain;

import com.carhelper.model.DiagnosticReport;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageDiagnosisChain {
    private final ImageFileValidationHandler fileValidationHandler;
    private final ImageSizeValidationHandler sizeValidationHandler;
    private final ImageAiAnalysisHandler aiAnalysisHandler;

    public ImageDiagnosisChain(ImageFileValidationHandler fileValidationHandler, ImageSizeValidationHandler sizeValidationHandler, ImageAiAnalysisHandler aiAnalysisHandler) {
        this.fileValidationHandler = fileValidationHandler;
        this.sizeValidationHandler = sizeValidationHandler;
        this.aiAnalysisHandler = aiAnalysisHandler;
    }

    public DiagnosticReport process(MultipartFile file, String language) {
        fileValidationHandler.setNext(sizeValidationHandler);
        sizeValidationHandler.setNext(aiAnalysisHandler);
        ImageDiagnosisRequest request = new ImageDiagnosisRequest(file, language);
        fileValidationHandler.handle(request);
        return request.getReport();
    }
}
