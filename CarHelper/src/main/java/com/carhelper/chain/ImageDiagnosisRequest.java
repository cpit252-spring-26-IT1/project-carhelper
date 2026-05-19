package com.carhelper.chain;

import com.carhelper.model.DiagnosticReport;
import org.springframework.web.multipart.MultipartFile;

public class ImageDiagnosisRequest {
    private MultipartFile file;
    private String language;
    private String rawAiResponse;
    private DiagnosticReport report;

    public ImageDiagnosisRequest(MultipartFile file, String language) {
        this.file = file;
        this.language = language;
    }

    public MultipartFile getFile() {
        return file;
    }

    public String getLanguage() {
        return language;
    }

    public String getRawAiResponse() {
        return rawAiResponse;
    }

    public void setRawAiResponse(String rawAiResponse) {
        this.rawAiResponse = rawAiResponse;
    }

    public DiagnosticReport getReport() {
        return report;
    }

    public void setReport(DiagnosticReport report) {
        this.report = report;
    }
}
