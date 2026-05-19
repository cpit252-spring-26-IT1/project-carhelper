package com.carhelper.chain;

import org.springframework.stereotype.Component;

@Component
public class ImageSizeValidationHandler extends AbstractDiagnosisHandler {

    @Override
    public void handle(ImageDiagnosisRequest request) {
        long maxSize = 5 * 1024 * 1024;
        if (request.getFile().getSize() > maxSize) {
            throw new IllegalArgumentException("Image size must be less than 5MB.");
        }
        handleNext(request);
    }
}
