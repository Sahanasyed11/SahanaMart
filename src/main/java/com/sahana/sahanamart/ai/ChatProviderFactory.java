package com.sahana.sahanamart.ai;

import com.sahana.sahanamart.util.ConfigUtil;

/**
 * Factory class for ChatProvider instantiation (Factory Design Pattern).
 */
public class ChatProviderFactory {
    private static ChatProvider instance;

    public static synchronized ChatProvider getProvider() {
        if (instance == null) {
            String providerType = ConfigUtil.get("ai.chatbot.provider", "mock").trim().toLowerCase();
            if ("gemini".equals(providerType)) {
                instance = new GeminiChatProvider();
            } else {
                instance = new MockChatProvider();
            }
        }
        return instance;
    }

    public static synchronized void setProvider(ChatProvider customProvider) {
        instance = customProvider;
    }
}
