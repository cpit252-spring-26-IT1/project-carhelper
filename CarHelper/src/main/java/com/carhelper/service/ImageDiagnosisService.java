package com.carhelper.service;

import com.carhelper.chain.ImageDiagnosisChain;
import com.carhelper.model.DiagnosticReport;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageDiagnosisService {
    private final ImageDiagnosisChain imageDiagnosisChain;

    public ImageDiagnosisService(ImageDiagnosisChain imageDiagnosisChain) {
        this.imageDiagnosisChain = imageDiagnosisChain;
    }

    public DiagnosticReport analyze(MultipartFile file, String language) {
        return imageDiagnosisChain.process(file, language);
    }
}