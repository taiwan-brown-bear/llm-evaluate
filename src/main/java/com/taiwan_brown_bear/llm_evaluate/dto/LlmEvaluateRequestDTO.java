package com.taiwan_brown_bear.llm_evaluate.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LlmEvaluateRequestDTO {
    private Integer requestId;
    private String request;
    private String systemMessage;
}
