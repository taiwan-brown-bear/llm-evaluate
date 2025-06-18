package com.taiwan_brown_bear.llm_evaluate.dto;

public class LlmEvaluateResponseDTO {
    private Integer requestId;

    private String request;

    private String systemMessage;

    public LlmEvaluateResponseDTO(Integer requestId, String request, String systemMessage) {
        this.requestId = requestId;
        this.request = request;
        this.systemMessage = systemMessage;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getSystemMessage() {
        return systemMessage;
    }

    public void setSystemMessage(String systemMessage) {
        this.systemMessage = systemMessage;
    }
}
