package com.carhelper.controller;

import com.carhelper.command.ResaleValueCommand;
import com.carhelper.dto.ResaleValueRequest;
import com.carhelper.service.HistoryService;
import com.carhelper.service.ResaleValueService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cost")
@CrossOrigin(origins = "*")
public class ResaleValueController {
    private final ResaleValueService resaleValueService;
    private final HistoryService historyService;

    public ResaleValueController(ResaleValueService resaleValueService, HistoryService historyService) {
        this.resaleValueService = resaleValueService;
        this.historyService = historyService;
    }

    @PostMapping("/resale")
    public String estimate(@RequestBody ResaleValueRequest request) {
        ResaleValueCommand command = new ResaleValueCommand(resaleValueService, request);
        String result = command.execute();

        System.out.println("RESALE USER ID = " + request.getUserId());

        String input = request.getBrand() + " " + request.getModel() + " " + request.getYear()
                + ", mileage: " + request.getMileage()
                + ", condition: " + request.getCondition()
                + ", problems: " + request.getMechanicalProblems();

        historyService.saveHistory(request.getUserId(), "Car Valuation", input, result);

        return result;
    }
}
