package com.taiwan_brown_bear.llm_evaluate.service.llm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taiwan_brown_bear.llm_evaluate.dto.LlmEvaluateResultDTO;
import com.taiwan_brown_bear.llm_evaluate.template.LlmPromptSystemMessageTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.retry.NonTransientAiException;

import java.util.List;

@Slf4j
public abstract class LlmModel {

    protected ChatClient chatClient;

    private static ObjectMapper objectMapper = new ObjectMapper();

    public LlmEvaluateResultDTO getResponse(String systemMsg, String userMsg)
    {
        SystemMessage systemMessage = new SystemMessage(systemMsg == null ? "" : systemMsg);
        UserMessage userMessage = new UserMessage(userMsg);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();

        final String model    = chatResponse.getMetadata().getModel();
        final String response = chatResponse.getResult().getOutput().getText();

        log.info("from model, {}, we got response, {}", model, response);
        return LlmEvaluateResultDTO.builder()
                .targetModel(model)
                .targetModelResponse(response)
                .build();
    }

    public LlmEvaluateResultDTO getEvaluationResult(String originalPrompt, String response)
    {
        // step 1: prepare the prompt
        String promptToEvaluate = LlmPromptSystemMessageTemplate.getGuidelineForEvaluation(originalPrompt, response, LlmPromptSystemMessageTemplate.EvaluationResult.JSON_FORMAT);
        log.debug("promptToEvaluate: " + promptToEvaluate);
        final String evalSystemMsg = promptToEvaluate;
        final String evalUserMsg   = promptToEvaluate;

        // step 2: ask llm to evaluate
        SystemMessage evaluationSystemMessage = new SystemMessage(evalSystemMsg == null ? "" : evalSystemMsg);
        UserMessage   evaluationUserMessage   = new UserMessage  (evalUserMsg);
        Prompt prompt = new Prompt(List.of(evaluationSystemMessage, evaluationUserMessage));

        ChatResponse evaluationModelChatResponse = null;
        try {
            evaluationModelChatResponse = chatClient.prompt(prompt).call().chatResponse();
        } catch (NonTransientAiException e) {
            log.warn("might exceed the allowed rate ...", e);
            return LlmEvaluateResultDTO.builder()
                    .guidelineForEvaluation(evalSystemMsg)
                    .issues(List.of(e.getMessage()))
                    .build();
        } finally {
            log.debug("evaluationModelChatResponse: {}", evaluationModelChatResponse);
        }

        // step 3: get eval. response
        final String evaluationModel         = evaluationModelChatResponse.getMetadata().getModel();
        String evaluationModelResponse = evaluationModelChatResponse.getResult().getOutput().getText();

        // step 4: parse the json response
        LlmPromptSystemMessageTemplate.EvaluationResult evaluationResult = null;
        try {
            evaluationResult = objectMapper.readValue(evaluationModelResponse, LlmPromptSystemMessageTemplate.EvaluationResult.class);
        } catch (JsonProcessingException e) {

            boolean isSuc = false;
            try {
            // for Anthropic,
            // "```json\n{\n    \"isValid\": true,\n    \"issues\": [],\n    \"confidence\": 0.98\n}\n```"
            if(evaluationModelResponse != null               &&
               evaluationModelResponse.startsWith("```json") &&
               evaluationModelResponse.endsWith  ("```")     ){
                evaluationModelResponse = evaluationModelResponse.substring("```json".length());
                evaluationModelResponse = evaluationModelResponse.substring(0, evaluationModelResponse.length() - "```".length());
                evaluationResult = objectMapper.readValue(evaluationModelResponse, LlmPromptSystemMessageTemplate.EvaluationResult.class);
                isSuc = true;
            }
            } catch (JsonProcessingException e1) {
                log.warn("abc failed to parse the response, {}", evaluationModelResponse, e1);
            }
            if(!isSuc) {

                log.warn("failed to parse the response, {}", evaluationModelResponse, e);
                return LlmEvaluateResultDTO.builder()
                        .evaluatedBy(evaluationModel)
                        .guidelineForEvaluation(evalSystemMsg)
                        .evaluationResult(evaluationModelResponse)
                        .issues(List.of(e.getMessage()))
                        .build();
            }
        } finally {





            log.debug("evaluationResult: {}", evaluationResult);
        }

        // step 5: result the result
        return LlmEvaluateResultDTO.builder()
                .evaluatedBy           (evaluationModel)
                .guidelineForEvaluation(evalSystemMsg)
                .evaluationResult      (evaluationModelResponse)
                .isValid               (evaluationResult.getIsValid())
                .issues                (evaluationResult.getIssues())
                .confidence            (evaluationResult.getConfidence())
                .build();
    }
}
