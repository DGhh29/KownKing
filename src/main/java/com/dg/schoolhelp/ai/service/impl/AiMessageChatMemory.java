package com.dg.schoolhelp.ai.service.impl;

import com.dg.schoolhelp.ai.entity.AiMessage;
import com.dg.schoolhelp.ai.mapper.AiMessageMapper;
import com.dg.schoolhelp.ai.mapper.AiSessionMapper;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.model.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AiMessageChatMemory implements ChatMemory {

    @Autowired
    private AiMessageMapper aiMessageMapper;

    @Override
    public void add(String conversationId, List<Message> messages) {

    }

    @Override
    public List<Message> get(String conversationId, int lastN) {

        return aiMessageMapper
                .findTenTextBySessionId(conversationId,lastN)
                .stream()
                .map(AiMessageChatMemory::toSpringAiMessage)
                .toList();
    }

    @Override
    public void clear(String conversationId) {

    }

    // 1为用户消息，2为助手消息，3为系统消息
    public static Message toSpringAiMessage(AiMessage aiMessage) {
        List<Media> mediaList = new ArrayList<>();
//        if (!CollectionUtil.isEmpty(aiMessage.medias())) {
//            mediaList = aiMessage.medias().stream().map(AiMessageChatMemory::toSpringAiMedia).toList();
//        }
        if (aiMessage.getType() == null || aiMessage.getType().isEmpty()) {
            // 返回一个默认的 Message 对象，而不是 null
            return new AssistantMessage("");  // 这里返回一个空的 AssistantMessage，避免返回 null
        }
        switch (aiMessage.getType()) {
            case "2" -> {
                return new AssistantMessage(aiMessage.getTextContent());
            }
            case "1" -> {
                return new UserMessage(aiMessage.getTextContent(), mediaList);
            }
            case "3" -> {
                return new SystemMessage(aiMessage.getTextContent());
            }
        }
        return new AssistantMessage("");
    }
}
