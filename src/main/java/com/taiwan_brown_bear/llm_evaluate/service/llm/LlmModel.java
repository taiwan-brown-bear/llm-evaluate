package com.taiwan_brown_bear.llm_evaluate.service.llm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.taiwan_brown_bear.llm_evaluate.dto.LlmEvaluateResultDTO;
import com.taiwan_brown_bear.llm_evaluate.service.template.LlmPromptGuidelineTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.retry.NonTransientAiException;

import java.util.List;

@Slf4j
public abstract class LlmModel
{
    protected        ChatClient   chatClient;

    public LlmEvaluateResultDTO getResponse(String systemMsg, String userMsg) {
        SystemMessage systemMessage = new SystemMessage(systemMsg == null ? "" : systemMsg);
        UserMessage   userMessage   = new UserMessage(userMsg);
        Prompt        prompt        = new Prompt(List.of(systemMessage, userMessage));
        ChatResponse  chatResponse  = chatClient.prompt(prompt).call().chatResponse();
        final String model    = chatResponse.getMetadata().getModel();
        final String response = chatResponse.getResult().getOutput().getText();
        log.info("model, {}, returns response, {}", model, response);
        return LlmEvaluateResultDTO.builder().targetModel(model).targetModelResponse(response).build();
    }

    public LlmEvaluateResultDTO getResult(LlmPromptGuidelineTemplate llmPromptGuidelineTemplate, String originalPrompt, String response) {

        // step 1: prepare the evaluation prompt (ask llm to return json format)
        //
        String promptToEvaluate = llmPromptGuidelineTemplate.getGuideline(originalPrompt, response);
        log.debug("promptToEvaluate: " + promptToEvaluate);
        final String evalSystemMsg = promptToEvaluate;
        final String evalUserMsg   = promptToEvaluate;

        // step 2: ask llm to evaluate
        //
        SystemMessage evaluationSystemMessage = new SystemMessage (evalSystemMsg == null ? "" : evalSystemMsg);
        UserMessage   evaluationUserMessage   = new UserMessage   (evalUserMsg);
        Prompt        prompt                  = new Prompt(List.of(evaluationSystemMessage, evaluationUserMessage));

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

        // step 3: get evaluation result
        //
        final String evaluationModel         = evaluationModelChatResponse.getMetadata().getModel();
        String evaluationModelResponse = evaluationModelChatResponse.getResult().getOutput().getText();

        // step 4: parse the json response
        //
        LlmPromptGuidelineTemplate evaluationResult = null;
        try {
            evaluationResult = llmPromptGuidelineTemplate.parseResponse(prepareResponse(evaluationModelResponse));
        } catch (JsonProcessingException jpe) {
            log.warn("failed to parse the response, {}", evaluationModelResponse, jpe);
            return LlmEvaluateResultDTO.builder()
                    .evaluatedBy(evaluationModel)
                    .guidelineForEvaluation(evalSystemMsg)
                    .evaluationResult(evaluationModelResponse)
                    .issues(List.of(jpe.getMessage()))
                    .build();
        } catch (Exception e) {
            log.warn("failed to parse the response, {}, due to an unexpected exception ...", evaluationModelResponse, e);
        }

        // step 5: result the result
        //
        return LlmEvaluateResultDTO.builder()
                .evaluatedBy           (evaluationModel)
                .guidelineForEvaluation(evalSystemMsg)
                .evaluationResult      (evaluationModelResponse)
                .isValid               (evaluationResult.getIsValid())
                .issues                (evaluationResult.getIssues())
                .confidence            (evaluationResult.getConfidence())
                .build();
    }

    public String prepareResponse(String response) {
        return response;
    }
}
