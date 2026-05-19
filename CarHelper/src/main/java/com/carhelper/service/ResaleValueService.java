package com.carhelper.service;

import com.carhelper.ai.TextAiService;
import com.carhelper.dto.ResaleValueRequest;
import com.carhelper.strategy.ResaleValueCalculatorContext;
import org.springframework.stereotype.Service;

@Service
public class ResaleValueService {
    private final TextAiService textAiService;
    private final ResaleValueCalculatorContext calculatorContext;

    public ResaleValueService(TextAiService textAiService, ResaleValueCalculatorContext calculatorContext) {
        this.textAiService = textAiService;
        this.calculatorContext = calculatorContext;
    }

    public String estimate(ResaleValueRequest request) {
        String prompt = calculatorContext.buildPrompt(request);

        prompt = prompt + """

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
                Do not use tables.
                Do not write a long explanation.
                Keep the answer short and clear.
                """;

        String result = textAiService.analyzeText(prompt, request.getLanguage());
        return cleanAiResult(result);
    }

    private String cleanAiResult(String result) {
        if (result == null) {
            return "";
        }

        return result
                .replace("###", "")
                .replace("##", "")
                .replace("#", "")
                .replace("**", "")
                .replace("*", "")
                .replace("---", "")
                .replace("S A R", "SAR")
                .replace("Estimated Price:", "\nEstimated Price:")
                .replace("Condition Summary:", "\n\nCondition Summary:")
                .replace("Main Reasons:", "\n\nMain Reasons:")
                .replace("1.", "\n1.")
                .replace("2.", "\n2.")
                .replace("3.", "\n3.")
                .replace("Advice:", "\n\nAdvice:")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }
}
