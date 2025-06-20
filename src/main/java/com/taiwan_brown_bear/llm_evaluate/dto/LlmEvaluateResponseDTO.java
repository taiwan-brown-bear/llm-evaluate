package com.taiwan_brown_bear.llm_evaluate.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class LlmEvaluateResponseDTO {
    private Integer requestId;
    private String request;
    private String systemMessage;
    private String targetModel;
    private String targetModelResponse;
    private List<LlmEvaluateResultDTO> targetModelEvaluationResults;
}
