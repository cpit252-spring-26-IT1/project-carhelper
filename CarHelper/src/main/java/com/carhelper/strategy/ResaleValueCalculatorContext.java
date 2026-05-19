package com.carhelper.strategy;

import com.carhelper.dto.ResaleValueRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResaleValueCalculatorContext {
    private final List<ResaleValueStrategy> strategies;

    public ResaleValueCalculatorContext(List<ResaleValueStrategy> strategies) {
        this.strategies = strategies;
    }

    public String buildPrompt(ResaleValueRequest request) {
        for (ResaleValueStrategy strategy : strategies) {
            if (strategy.supports(request.getCondition())) {
                return strategy.buildPrompt(request);
            }
        }

        return """
                Estimate the resale value for this car in the Saudi market.

                Car details:
                Brand: %s
                Model: %s
                Year: %s
                Mileage: %s
                Condition: %s
                Mechanical problems: %s

                Return the answer in this exact format only:

                Estimated Price: SAR X - SAR Y
                Condition Summary: one short sentence
                Main Reasons:
                1. reason one
                2. reason two
                3. reason three
                Advice: one short sentence

                Do not use markdown.
                Do not use stars.
                Do not use hashtags.
                Do not use bullet points.
                Do not use tables.
                Do not write a long explanation.
                Keep the answer short and clear.
                Write in %s.
                """.formatted(
                request.getBrand(),
                request.getModel(),
                request.getYear(),
                request.getMileage(),
                request.getCondition(),
                request.getMechanicalProblems(),
                language(request.getLanguage())
        );
    }

    private String language(String value) {
        return "ar".equalsIgnoreCase(value) ? "Arabic" : "English";
    }
}