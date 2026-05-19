package com.carhelper.chain;

public interface DiagnosisHandler {
    void setNext(DiagnosisHandler next);
    void handle(ImageDiagnosisRequest request);
}
