package com.taiwan_brown_bear.llm_evaluate.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class LlmEvaluateResultDTO {

    private String       targetModel;
    private String       targetModelResponse;
    private String       evaluatedBy;
    private String       guidelineForEvaluation;
    private String       evaluationResult;
    private Boolean      isValid;
    private List<String> issues;
    private Double       confidence;

}
