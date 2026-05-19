package com.carhelper.command;

import com.carhelper.dto.RepairCostRequest;
import com.carhelper.service.RepairCostService;

public class RepairCostCommand implements DiagnosisCommand<String> {
    private final RepairCostService service;
    private final RepairCostRequest request;

    public RepairCostCommand(RepairCostService service, RepairCostRequest request) {
        this.service = service;
        this.request = request;
    }

    @Override
    public String execute() {
        return service.estimate(request);
    }
}