package com.carhelper.strategy;

import com.carhelper.dto.ResaleValueRequest;

public interface ResaleValueStrategy {
    boolean supports(String condition);

    String buildPrompt(ResaleValueRequest request);
}