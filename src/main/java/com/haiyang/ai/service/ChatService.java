package com.haiyang.ai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@Service
public class ChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private final ChatModel chatModel;
    private final PromptManagementService promptManagementService;

    public ChatService(ChatModel chatModel, PromptManagementService promptManagementService) {
        this.chatModel = chatModel;
        this.promptManagementService = promptManagementService;
    }

    public String establishChat() {
        String chatId = UUID.randomUUID().toString();
        logger.debug("Establishing chat with chatId: {}", chatId);
        promptManagementService.establishChat(chatId);
        return chatId;
    }

    public Flux<ChatResponse> chatV2(String chatId, String message) {
        Message systemMessage = promptManagementService.getSystemMessage(chatId, message);
        UserMessage userMessage = new UserMessage(message);
        promptManagementService.addMessage(chatId, userMessage);
        logger.debug("Chatting with chatId: {} and message: {}", chatId, message);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        logger.info("prompt: {}", prompt);
        return chatModel.stream(prompt);
    }

    public Flux<ChatResponse> chat(String chatId, String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt);
    }
}