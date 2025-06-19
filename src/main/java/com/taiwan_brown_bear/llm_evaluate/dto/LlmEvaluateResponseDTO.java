package com.taiwan_brown_bear.llm_evaluate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LlmEvaluateResponseDTO {
    private Integer requestId;
    private String request;
    private String systemMessage;
}
