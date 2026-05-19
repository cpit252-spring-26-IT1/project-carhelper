package com.carhelper.strategy;

import com.carhelper.dto.RepairCostRequest;
import org.springframework.stereotype.Component;

@Component
public class PaintRepairCostStrategy implements RepairCostStrategy {
    @Override
    public String buildPrompt(RepairCostRequest request) {
        return "Estimate paint or body repair cost in Saudi Arabia for " + request.getBrand() +
                " " + request.getModel() + " " + request.getYear() + ". Damage or symptoms: " + request.getSymptoms()   +
                ". Give cost range in SAR, " +
                "repair steps, and advice. " +
                "Do not use fixed hardcoded app prices. " +
                "Do not use markdown.\n" +
                "Do not use stars.\n" +
                "Do not use hashtags.\n" +
                "Do not use tables.\n" +
                "Do not use bullet points.\n" +
                "Do not write a long explanation.\n" +
                "Keep the answer short and clear.\n" +
                " write normal do not use bold.\n" + language(request.getLanguage()) + ".";
    }

    private String language(String value) {
        return "ar".equalsIgnoreCase(value) ? "Arabic" : "English";
    }
}
