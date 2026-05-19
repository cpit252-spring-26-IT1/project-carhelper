package com.carhelper.strategy;

import com.carhelper.dto.RepairCostRequest;
import org.springframework.stereotype.Component;

@Component
public class RepairCostEstimatorContext {
    private final EngineRepairCostStrategy engineStrategy;
    private final PaintRepairCostStrategy paintStrategy;
    private final ElectricalRepairCostStrategy electricalStrategy;
    private final GeneralRepairCostStrategy generalStrategy;

    public RepairCostEstimatorContext(EngineRepairCostStrategy engineStrategy, PaintRepairCostStrategy paintStrategy, ElectricalRepairCostStrategy electricalStrategy, GeneralRepairCostStrategy generalStrategy) {
        this.engineStrategy = engineStrategy;
        this.paintStrategy = paintStrategy;
        this.electricalStrategy = electricalStrategy;
        this.generalStrategy = generalStrategy;
    }

    public String buildPrompt(RepairCostRequest request) {
        String type = request.getRepairType() == null ? "" : request.getRepairType().toLowerCase();
        if (type.contains("engine")) {
            return engineStrategy.buildPrompt(request);
        }
        if (type.contains("paint") || type.contains("body")) {
            return paintStrategy.buildPrompt(request);
        }
        if (type.contains("electrical") || type.contains("electric")) {
            return electricalStrategy.buildPrompt(request);
        }
        return generalStrategy.buildPrompt(request);
    }
}
