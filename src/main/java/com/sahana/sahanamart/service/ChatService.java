package com.sahana.sahanamart.service;

import com.sahana.sahanamart.ai.ChatProvider;
import com.sahana.sahanamart.ai.ChatProviderFactory;
import com.sahana.sahanamart.dto.ChatResponseDTO;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.util.ConfigUtil;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service managing Chat operations, per-session rate limiting (10 req/min),
 * input validation, and per-session response caching.
 */
public class ChatService {
    private final ChatProvider chatProvider;
    private static final int MAX_INPUT_LENGTH = 250;
    private static final int RATE_LIMIT_MAX = 10;
    private static final long RATE_LIMIT_WINDOW_MS = 60_000L; // 1 minute

    // Per-session request timestamps for sliding window rate limiter
    private static final Map<String, LinkedList<Long>> rateLimitMap = new ConcurrentHashMap<>();

    // Per-session cache for repeated identical queries
    private static final Map<String, Map<String, String>> sessionCacheMap = new ConcurrentHashMap<>();

    public ChatService() {
        this.chatProvider = ChatProviderFactory.getProvider();
    }

    public ChatService(ChatProvider chatProvider) {
        this.chatProvider = chatProvider;
    }

    public ChatResponseDTO processChat(String sessionId, String userMessage) {
        // 1. Input validation & length cap
        if (userMessage == null || userMessage.trim().isEmpty()) {
            throw new ValidationException("Message cannot be empty");
        }
        String cleanMessage = userMessage.trim();
        int maxLen = ConfigUtil.getInt("ai.chatbot.maxInputLength", MAX_INPUT_LENGTH);
        if (cleanMessage.length() > maxLen) {
            throw new ValidationException("Message exceeds maximum allowed length (" + maxLen + " characters)");
        }

        // 2. Enforce per-session sliding window rate limit
        int rateLimit = ConfigUtil.getInt("ai.chatbot.rateLimitPerMinute", RATE_LIMIT_MAX);
        if (!checkRateLimit(sessionId, rateLimit)) {
            return new ChatResponseDTO("Rate limit exceeded: You can send at most " + rateLimit + " messages per minute. Please wait a moment.",
                    "RateLimiter", false);
        }

        // 3. In-memory per-session cache check
        Map<String, String> cache = sessionCacheMap.computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>());
        String normalizedKey = cleanMessage.toLowerCase();
        if (cache.containsKey(normalizedKey)) {
            return new ChatResponseDTO(cache.get(normalizedKey), chatProvider.getProviderName(), true);
        }

        // 4. Delegate to provider with try/catch fallback
        String reply;
        try {
            reply = chatProvider.generateResponse(cleanMessage);
        } catch (Exception e) {
            reply = "I apologize, but our assistant is currently experiencing high load. For urgent questions, please email support@sahanamart.com.";
        }

        // Store in cache
        cache.put(normalizedKey, reply);
        return new ChatResponseDTO(reply, chatProvider.getProviderName(), false);
    }

    private synchronized boolean checkRateLimit(String sessionId, int maxRequests) {
        long now = System.currentTimeMillis();
        LinkedList<Long> timestamps = rateLimitMap.computeIfAbsent(sessionId, k -> new LinkedList<>());

        // Evict timestamps older than 1 minute
        while (!timestamps.isEmpty() && (now - timestamps.peekFirst() > RATE_LIMIT_WINDOW_MS)) {
            timestamps.pollFirst();
        }

        if (timestamps.size() >= maxRequests) {
            return false;
        }

        timestamps.addLast(now);
        return true;
    }

    public static void clearSession(String sessionId) {
        rateLimitMap.remove(sessionId);
        sessionCacheMap.remove(sessionId);
    }
}
