package com.taiwan_brown_bear.llm_evaluate.dto;

import java.util.ArrayList;
import java.util.List;

public record LlmModelResponseDTO(String model, String sysMsg, String resp, Boolean isValid, List<String> issues, String confidence) {
    public LlmModelResponseDTO(String model, String sysMsg, String resp){
        this(model, sysMsg, resp, null, null, null);
    }
    public LlmModelResponseDTO(String errorMsg){
        this(null, null, null, false, List.of(errorMsg), null);
    }
}
