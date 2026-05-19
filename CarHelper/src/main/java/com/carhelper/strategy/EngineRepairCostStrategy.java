package com.carhelper.strategy;

import com.carhelper.dto.RepairCostRequest;
import org.springframework.stereotype.Component;

@Component
public class EngineRepairCostStrategy implements RepairCostStrategy {
    @Override
    public String buildPrompt(RepairCostRequest request) {
        return "Estimate the engine repair cost in Saudi Arabia for " + request.getBrand() + " " + request.getModel() + " " + request.getYear() + ". Symptoms: " + request.getSymptoms()  + ". Give likely issue, repair steps, estimated cost range in SAR, and advice. Do not use fixed hardcoded app prices. Write in " + language(request.getLanguage()) + ".";
    }

    private String language(String value) {
        return "ar".equalsIgnoreCase(value) ? "Arabic" : "English";
    }
}
