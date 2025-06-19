package com.taiwan_brown_bear.llm_evaluate.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class LlmEvaluateResponseDTO {
    private Integer requestId;
    private String request;
    private String  systemMessage;
    private String answerModel;
    private String answerResponse;
    private List<LlmModelResponseDTO> evaluationModelResults;
}
