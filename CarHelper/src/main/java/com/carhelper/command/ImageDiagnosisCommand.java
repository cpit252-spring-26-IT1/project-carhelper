package com.carhelper.command;

import com.carhelper.model.DiagnosticReport;
import com.carhelper.service.ImageDiagnosisService;
import org.springframework.web.multipart.MultipartFile;

public class ImageDiagnosisCommand implements DiagnosisCommand<DiagnosticReport> {
    private final ImageDiagnosisService service;
    private final MultipartFile file;
    private final String language;

    public ImageDiagnosisCommand(ImageDiagnosisService service, MultipartFile file, String language) {
        this.service = service;
        this.file = file;
        this.language = language;
    }

    @Override
    public DiagnosticReport execute() {
        return service.analyze(file, language);
    }
}