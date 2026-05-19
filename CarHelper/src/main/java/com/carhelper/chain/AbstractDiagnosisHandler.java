package com.carhelper.chain;

public abstract class AbstractDiagnosisHandler implements DiagnosisHandler {
    private DiagnosisHandler next;

    @Override
    public void setNext(DiagnosisHandler next) {
        this.next = next;
    }

    protected void handleNext(ImageDiagnosisRequest request) {
        if (next != null) {
            next.handle(request);
        }
    }
}
