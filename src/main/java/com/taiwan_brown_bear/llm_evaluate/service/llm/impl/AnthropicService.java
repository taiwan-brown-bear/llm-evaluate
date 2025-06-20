package com.taiwan_brown_bear.llm_evaluate.service.llm.impl;

import com.taiwan_brown_bear.llm_evaluate.service.llm.LlmModel;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AnthropicService extends LlmModel {

    // we want spring to inject chatModel for us ...
    public AnthropicService(AnthropicChatModel chatModel){
        this.chatClient = ChatClient.create(chatModel);
    }

    @Override
    public String prepareResponse(String response){
        // for Anthropic, from the observation,
        // it might return a json as the following
        //
        // "```json\n{\n    \"isValid\": true,\n    \"issues\": [],\n    \"confidence\": 0.98\n}\n```"
        // or       "{\n    \"isValid\": true,\n    \"issues\": [],\n    \"confidence\": 1.0\n}"
        //
        if (response != null               &&
            response.startsWith("```json") &&
            response.endsWith  ("```")     ){
            response = response.substring("```json".length());
            response = response.substring(0, response.length() - "```".length());
        }
        return response;
    }
}
