package com.sahana.sahanamart.ai;

/**
 * Strategy interface for AI Chatbot providers.
 * Complies with Section 17: Pluggable ChatProvider interface allowing Mock and real LLMs.
 */
public interface ChatProvider {
    String generateResponse(String userMessage);
    String getProviderName();
}
