package com.taiwan_brown_bear.llm_evaluate.service.template.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taiwan_brown_bear.llm_evaluate.service.template.LlmPromptGuidelineTemplate;

public class LlmPromptGuidelineTemplateForModelEvaluation extends LlmPromptGuidelineTemplate {

    protected static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getGuideline(String originalPrompt, String response) {
        return String.format(
                    """
                    You are a validation system. Analyze the following response to the prompt:
                    Original Prompt: %s
                    Response to Validate: %s

                    Check for:
                    1. Logical consistency
                    2. Factual impossibilities
                    3. Temporal contradictions

                    Respond with a JSON object containing:
                    """
                    +
                    RESPONSE_FORMAT
                    ,
                originalPrompt,
                response);
    }

    @Override
    public LlmPromptGuidelineTemplate parseResponse(String modelResponse) throws JsonProcessingException {
        return objectMapper.readValue(modelResponse, this.getClass());
    }

}
