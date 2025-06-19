package com.taiwan_brown_bear.llm_evaluate.template;

public class LlmPromptSystemMessageTemplate {

    public static String getEvaluationPrompt(String originalPrompt, String response) {
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
                    {
                       "isValid": boolean,
                       "issues": [list of specific issues found],
                       "confidence": number between 0 and 1
                    }
                    """,
                originalPrompt,
                response);
    }

}
