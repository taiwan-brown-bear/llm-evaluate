package com.taiwan_brown_bear.llm_evaluate.dto;

import java.util.ArrayList;
import java.util.List;

public record LlmEvaluateResultDTO(
        String targetModel,
        String targetModelResponse,
        String evaluatedBy,
        String guidelineForEvaluation,
        String evaluationResult,
        Boolean isValid,
        List<String> issues,
        Double confidence) {
    public LlmEvaluateResultDTO(String targetModel, String targetModelResponse, String evaluatedBy, String guidelineForEvaluation, String evaluationResult){
        this(targetModel, targetModelResponse, evaluatedBy, guidelineForEvaluation, evaluationResult, null, null, null);
    }
    public LlmEvaluateResultDTO(String targetModel, String targetModelResponse, String issue){
        this(targetModel, targetModelResponse, null, null, null, false, List.of(issue), null);
    }
}
