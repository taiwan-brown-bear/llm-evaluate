package com.taiwan_brown_bear.llm_evaluate.service.llm.impl;

import com.taiwan_brown_bear.llm_evaluate.service.llm.LlmModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

@Service
public class OpenAIService extends LlmModel {

    // we want spring to inject chatModel for us ...
    public OpenAIService(OpenAiChatModel chatModel){
        this.chatClient = ChatClient.create(chatModel);
    }

}
