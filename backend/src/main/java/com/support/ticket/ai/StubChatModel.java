package com.support.ticket.ai;

import java.util.List;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.AssistantMessage;

public class StubChatModel implements ChatModel {

    @Override
    public ChatResponse call(Prompt prompt) {
        String text = "Based on the provided ticket excerpts, here is a grounded summary. [TKT-1001]";
        return new ChatResponse(List.of(new Generation(new AssistantMessage(text))));
    }
}
