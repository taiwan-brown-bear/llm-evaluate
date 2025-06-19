package com.taiwan_brown_bear.llm_evaluate.service.llm;

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

    public String getResponse(String systemMsg, String userMsg)
    {
        SystemMessage systemMessage = new SystemMessage(systemMsg == null ? "" : systemMsg);
        UserMessage userMessage = new UserMessage(userMsg);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();

        final String model    = chatResponse.getMetadata().getModel();
        final String response = chatResponse.getResult().getOutput().getText();

        log.info("from model, {}}, we got response, {}", model, response);
        return response;
    }

    public String getEvaluationResult(String originalPrompt, String response)
    {
        String promptToEvaluate = LlmPromptSystemMessageTemplate.getEvaluationPrompt(originalPrompt, response);
        log.debug("promptToEvaluate: " + promptToEvaluate);
        return getResponse(promptToEvaluate, promptToEvaluate);
    }
}
