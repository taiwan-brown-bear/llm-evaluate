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

}
