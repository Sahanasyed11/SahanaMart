package com.sahana.sahanamart.ai;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Mock AI Chat Provider fulfilling the Week 9 requirements.
 * Provides instant responses to domain FAQ queries without requiring external network calls.
 */
public class MockChatProvider implements ChatProvider {
    private final Map<String[], String> faqKnowledgeBase = new LinkedHashMap<>();

    public MockChatProvider() {
        initKnowledgeBase();
    }

    private void initKnowledgeBase() {
        faqKnowledgeBase.put(
                new String[]{"track", "status", "where is my order", "order status", "delivery status"},
                "📦 You can track your orders anytime by clicking 'Orders' in the top navigation bar. Statuses progress through PENDING ➔ CONFIRMED ➔ SHIPPED ➔ DELIVERED."
        );
        faqKnowledgeBase.put(
                new String[]{"shipping", "delivery time", "how long", "speed", "courier"},
                "🚚 Standard delivery across India takes 3 to 5 business days. Express delivery options are available at checkout."
        );
        faqKnowledgeBase.put(
                new String[]{"return", "refund", "exchange", "money back"},
                "🔄 We offer a hassle-free 7-day return policy for all eligible items. Once returned, refunds are processed within 2-3 business days to your original payment method."
        );
        faqKnowledgeBase.put(
                new String[]{"payment", "pay", "upi", "card", "cod", "cash on delivery"},
                "💳 SahanaMart supports Mock Credit/Debit Cards, UPI, Net Banking, and Cash on Delivery (COD). All transactions are encrypted and secured."
        );
        faqKnowledgeBase.put(
                new String[]{"seller", "sell", "register as seller", "vendor", "listing"},
                "💼 To sell products on SahanaMart, choose 'Seller' during registration. You will gain immediate access to the Seller Dashboard to list and manage products."
        );
        faqKnowledgeBase.put(
                new String[]{"review", "rating", "feedback", "star"},
                "⭐ Customers can submit 1 to 5 star ratings and reviews on items they have purchased. Look for the review section on any product details page."
        );
        faqKnowledgeBase.put(
                new String[]{"cancel", "cancellation"},
                "❌ You can cancel an order while it is in PENDING status directly from your Orders page. For orders already shipped, please use the return flow upon delivery."
        );
        faqKnowledgeBase.put(
                new String[]{"contact", "support", "help", "customer care", "phone", "email"},
                "📞 You can reach our dedicated support desk at support@sahanamart.com or call our toll-free helpline at 1800-SAHANA-MART (Mon-Sat, 9AM-6PM IST)."
        );
        faqKnowledgeBase.put(
                new String[]{"discount", "coupon", "offer", "sale"},
                "🎉 Explore our latest seasonal deals directly on the homepage catalog! All discounts are pre-applied to the displayed prices."
        );
        faqKnowledgeBase.put(
                new String[]{"safe", "security", "privacy", "password", "protect"},
                "🔒 Your security is our top priority. We use industry-standard jBCrypt salted hashing, parameterized queries, and SSL encryption to safeguard all your data."
        );
        faqKnowledgeBase.put(
                new String[]{"hi", "hello", "hey", "help", "who are you"},
                "👋 Hello! I am the SahanaMart AI Assistant. I can help you with product queries, order tracking, shipping info, returns, seller inquiries, and more. How may I assist you today?"
        );
    }

    @Override
    public String generateResponse(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Please ask a question regarding products, shipping, returns, or order tracking.";
        }

        String lower = userMessage.toLowerCase().trim();

        for (Map.Entry<String[], String> entry : faqKnowledgeBase.entrySet()) {
            for (String keyword : entry.getKey()) {
                if (lower.contains(keyword)) {
                    return entry.getValue();
                }
            }
        }

        // Domain-scoped fallback response
        return "I am the SahanaMart Virtual Assistant. I specialize in answering questions about our products, order tracking, delivery schedules, return policies, and seller registration. For additional questions, please contact support@sahanamart.com.";
    }

    @Override
    public String getProviderName() {
        return "MockChatProvider";
    }
}
