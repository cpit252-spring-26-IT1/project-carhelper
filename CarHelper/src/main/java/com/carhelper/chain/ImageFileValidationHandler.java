package com.carhelper.chain;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageFileValidationHandler extends AbstractDiagnosisHandler {

    @Override
    public void handle(ImageDiagnosisRequest request) {
        MultipartFile file = request.getFile();
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please upload an image file.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are accepted.");
        }
        handleNext(request);
    }
}
