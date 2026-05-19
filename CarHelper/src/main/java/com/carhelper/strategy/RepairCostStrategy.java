package com.carhelper.strategy;

import com.carhelper.dto.RepairCostRequest;

public interface RepairCostStrategy {
    String buildPrompt(RepairCostRequest request);
}
