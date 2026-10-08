package com.sahana.sahanamart.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.sahana.sahanamart.util.ConfigUtil;
import com.sahana.sahanamart.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Real Gemini LLM Provider connecting to Google Gemini REST API.
 * Complies with Section 11/17: Server-side API key only, connection timeout,
 * scoped system prompt, and graceful fallback to Mock provider.
 */
public class GeminiChatProvider implements ChatProvider {
    private static final Logger logger = LoggerFactory.getLogger(GeminiChatProvider.class);
    private final MockChatProvider fallbackProvider = new MockChatProvider();

    private static final String SYSTEM_INSTRUCTION =
            "You are the helpful, concise AI shopping assistant for SahanaMart, an online e-commerce platform. " +
            "Answer user questions politely and concisely regarding products, orders, shipping, returns, and sellers. " +
            "Keep responses under 3 sentences.";

    @Override
    public String generateResponse(String userMessage) {
        String apiKey = ConfigUtil.get("ai.chatbot.geminiApiKey", "");
        if (apiKey.isEmpty()) {
            apiKey = ConfigUtil.get("GEMINI_API_KEY", "");
        }

        if (apiKey.isEmpty() || apiKey.equalsIgnoreCase("YOUR_API_KEY_HERE")) {
            logger.debug("Gemini API key not configured. Using MockChatProvider fallback.");
            return fallbackProvider.generateResponse(userMessage);
        }

        String model = ConfigUtil.get("ai.chatbot.geminiModel", "gemini-1.5-flash");
        String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

        try {
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setConnectTimeout(5000); // 5 sec timeout
            conn.setReadTimeout(7000);    // 7 sec timeout
            conn.setDoOutput(true);

            String requestBody = "{\n" +
                    "  \"contents\": [{\n" +
                    "    \"parts\": [{\"text\": " + JsonUtil.toJson(SYSTEM_INSTRUCTION + "\n\nUser Question: " + userMessage) + "}]\n" +
                    "  }]\n" +
                    "}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line.trim());
                    }

                    JsonNode root = JsonUtil.getMapper().readTree(response.toString());
                    JsonNode candidates = root.path("candidates");
                    if (candidates.isArray() && candidates.size() > 0) {
                        String text = candidates.get(0).path("content").path("parts").get(0).path("text").asText();
                        if (text != null && !text.isEmpty()) {
                            return text.trim();
                        }
                    }
                }
            } else {
                logger.warn("Gemini API returned HTTP status {}. Falling back to mock answers.", code);
            }
        } catch (Exception e) {
            logger.warn("Gemini API call failed: {}. Falling back to MockChatProvider.", e.getMessage());
        }

        return fallbackProvider.generateResponse(userMessage);
    }

    @Override
    public String getProviderName() {
        return "GeminiChatProvider";
    }
}
