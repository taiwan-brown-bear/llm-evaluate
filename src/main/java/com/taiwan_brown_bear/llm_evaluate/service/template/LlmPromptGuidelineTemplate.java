package com.taiwan_brown_bear.llm_evaluate.service.template;

import java.util.List;
import lombok.Data;

@Data
public abstract class LlmPromptGuidelineTemplate
{
    public abstract String getGuideline(String request, String response);
    public abstract LlmPromptGuidelineTemplate parseResponse(String modelResponse) throws Exception;

    private Boolean      isValid;
    private List<String> issues;
    private Double       confidence;

    public static String RESPONSE_FORMAT =
            """
            {
                   "isValid": boolean,
                   "issues": [list of specific issues found],
                   "confidence": number between 0 and 1
            }
            """;
}
