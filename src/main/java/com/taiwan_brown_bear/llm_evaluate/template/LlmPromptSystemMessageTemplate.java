package com.taiwan_brown_bear.llm_evaluate.template;

import lombok.Data;

import java.util.List;

public class LlmPromptSystemMessageTemplate {

    @Data
    public static class EvaluationResult {
        public static final String JSON_FORMAT =
                """
                {
                       "isValid": boolean,
                       "issues": [list of specific issues found],
                       "confidence": number between 0 and 1
                }
                """;
        private Boolean      isValid;
        private List<String> issues;
        private String       confidence;
    }

    public static String getGuidelineForEvaluation(String originalPrompt, String response, String jsonFormat) {
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
                    jsonFormat
                    ,
                originalPrompt,
                response);
    }

}
