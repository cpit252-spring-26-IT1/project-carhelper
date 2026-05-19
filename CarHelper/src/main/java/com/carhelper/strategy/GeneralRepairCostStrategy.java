package com.carhelper.strategy;

import com.carhelper.dto.RepairCostRequest;
import org.springframework.stereotype.Component;

@Component
public class GeneralRepairCostStrategy implements RepairCostStrategy {
    @Override
    public String buildPrompt(RepairCostRequest request) {
        return "Estimate general repair cost in Saudi Arabia for " + request.getBrand() + " " + request.getModel() + " " + request.getYear() + ". Repair type: " + request.getRepairType() + ". Symptoms: " + request.getSymptoms() + ". Give possible cause, estimated cost range in SAR, repair advice, and AI disclaimer. Do not use fixed hardcoded app prices. Write in " + language(request.getLanguage()) + ".";
    }

    private String language(String value) {
        return "ar".equalsIgnoreCase(value) ? "Arabic" : "English";
    }
}
