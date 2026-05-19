package com.carhelper.strategy;

import com.carhelper.dto.RepairCostRequest;
import org.springframework.stereotype.Component;

@Component
public class ElectricalRepairCostStrategy implements RepairCostStrategy {
    @Override
    public String buildPrompt(RepairCostRequest request) {
        return "Estimate electrical repair cost in Saudi Arabia for " + request.getBrand() + " " + request.getModel() + " " + request.getYear() + ". Symptoms: " + request.getSymptoms() + ". Include possible electrical cause, cost range in SAR, and mechanic advice. Do not use fixed hardcoded app prices. Write in " + language(request.getLanguage()) + ".";
    }

    private String language(String value) {
        return "ar".equalsIgnoreCase(value) ? "Arabic" : "English";
    }
}
