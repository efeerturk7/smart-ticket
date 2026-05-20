package com.efeerturk.smart_ticket.ai;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LocalChatMemory implements ChatMemory {


    private final Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();

    @Override
    public void add(String conversationId, List<Message> messages) {
        conversationHistory.putIfAbsent(conversationId, new ArrayList<>());
        conversationHistory.get(conversationId).addAll(messages);
    }



    @Override
    public void clear(String conversationId) {
        conversationHistory.remove(conversationId);
    }


    public List<Message> get(String conversationId) {
        return conversationHistory.getOrDefault(conversationId, new ArrayList<>());
    }

}
