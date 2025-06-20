package com.taiwan_brown_bear.llm_evaluate.dto;

import java.util.ArrayList;
import java.util.List;

public record LlmEvaluateResultDTO(
        String evaluatedBy,
        String guidelineForEvaluation,
        String evaluationResult,
        Boolean isValid,
        List<String> issues,
        String confidence) {
    public LlmEvaluateResultDTO(String evaluatedBy, String guidelineForEvaluation, String evaluationResult){
        this(evaluatedBy, guidelineForEvaluation, evaluationResult, null, null, null);
    }
    public LlmEvaluateResultDTO(String issue){
        this(null, null, null, false, List.of(issue), null);
    }
}
