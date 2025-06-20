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
import java.util.List;

@Slf4j
public abstract class LlmModel {

    protected ChatClient chatClient;

    public LlmEvaluateResultDTO getResponse(String systemMsg, String userMsg)
    {
        SystemMessage systemMessage = new SystemMessage(systemMsg == null ? "" : systemMsg);
        UserMessage userMessage = new UserMessage(userMsg);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();

        final String model    = chatResponse.getMetadata().getModel();
        final String response = chatResponse.getResult().getOutput().getText();

        log.info("from model, {}, we got response, {}", model, response);
        return new LlmEvaluateResultDTO(model, systemMsg, response);
    }

    public LlmEvaluateResultDTO getEvaluationResult(String originalPrompt, String response)
    {
        String promptToEvaluate =
                LlmPromptSystemMessageTemplate.getGuidelineForEvaluation(
                        originalPrompt,
                        response,
                        LlmPromptSystemMessageTemplate.EvaluationResult.JSON_FORMAT);

        log.debug("promptToEvaluate: " + promptToEvaluate);

        String systemMsg = promptToEvaluate;
        String userMsg   = promptToEvaluate;

        SystemMessage systemMessage = new SystemMessage(systemMsg == null ? "" : systemMsg);
        UserMessage userMessage = new UserMessage(userMsg);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();

        final String evaluationModel         = chatResponse.getMetadata().getModel();
        final String evaluationModelResponse = chatResponse.getResult().getOutput().getText();

        ObjectMapper objectMapper = new ObjectMapper();
        LlmPromptSystemMessageTemplate.EvaluationResult evaluationResult = null;
        try {
            evaluationResult = objectMapper.readValue(evaluationModelResponse, LlmPromptSystemMessageTemplate.EvaluationResult.class);
        } catch (JsonProcessingException jsonProcessingException) {

        }

        log.debug("evaluationResult: {}", evaluationResult);
        if(evaluationResult == null) {
            return new LlmEvaluateResultDTO(evaluationModel, systemMsg, evaluationModelResponse);
        }
        return new LlmEvaluateResultDTO(
                evaluationModel,
                systemMsg,
                evaluationModelResponse,
                evaluationResult.getIsValid(),
                evaluationResult.getIssues(),
                evaluationResult.getConfidence()
                );
    }
}
