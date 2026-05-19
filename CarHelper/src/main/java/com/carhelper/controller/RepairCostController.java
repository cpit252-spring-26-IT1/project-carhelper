package com.carhelper.controller;

import com.carhelper.command.RepairCostCommand;
import com.carhelper.dto.RepairCostRequest;
import com.carhelper.service.HistoryService;
import com.carhelper.service.RepairCostService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cost")
@CrossOrigin(origins = "*")
public class RepairCostController {
    private final RepairCostService repairCostService;
    private final HistoryService historyService;

    public RepairCostController(RepairCostService repairCostService, HistoryService historyService) {
        this.repairCostService = repairCostService;
        this.historyService = historyService;
    }

    @PostMapping("/repair")
    public String estimate(@RequestBody RepairCostRequest request) {
        RepairCostCommand command = new RepairCostCommand(repairCostService, request);
        String result = command.execute();

        String input = request.getBrand() + " " + request.getModel() + " " + request.getYear()
                + ", repair type: " + request.getRepairType()
                + ", symptoms: " + request.getSymptoms();

        historyService.saveHistory(request.getUserId(), "Repair Cost", input, result);

        return result;
    }
}