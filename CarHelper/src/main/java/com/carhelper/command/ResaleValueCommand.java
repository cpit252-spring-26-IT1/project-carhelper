package com.carhelper.command;

import com.carhelper.dto.ResaleValueRequest;
import com.carhelper.service.ResaleValueService;

public class ResaleValueCommand implements DiagnosisCommand<String> {
    private final ResaleValueService service;
    private final ResaleValueRequest request;

    public ResaleValueCommand(ResaleValueService service, ResaleValueRequest request) {
        this.service = service;
        this.request = request;
    }

    @Override
    public String execute() {
        return service.estimate(request);
    }
}