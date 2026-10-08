<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- Floating AI Chatbot Widget (Week 10 Deliverable) -->
<button id="chatWidgetBtn" class="chat-widget-btn" title="Chat with AI Assistant">
    💬
</button>

<div id="chatPanel" class="chat-panel">
    <div class="chat-header">
        <h3>🤖 SahanaMart AI Assistant</h3>
        <button id="chatCloseBtn" class="chat-header-close">&times;</button>
    </div>
    
    <div id="chatMessages" class="chat-messages">
        <div class="chat-bubble chat-bubble-bot">
            👋 Hello! I am your SahanaMart shopping assistant. How can I help you today?
        </div>
    </div>

    <!-- Quick FAQ Pills -->
    <div class="chat-quick-replies">
        <button class="chat-pill" data-query="How can I track my order?">Track Order</button>
        <button class="chat-pill" data-query="What is your return policy?">Returns</button>
        <button class="chat-pill" data-query="What payment methods are supported?">Payments</button>
        <button class="chat-pill" data-query="How do I become a seller?">Sell Products</button>
    </div>

    <div class="chat-input-bar">
        <input type="text" id="chatInput" placeholder="Ask about shipping, returns, products..." maxlength="250">
        <button id="chatSendBtn" class="chat-send-btn">Send</button>
    </div>
</div>
