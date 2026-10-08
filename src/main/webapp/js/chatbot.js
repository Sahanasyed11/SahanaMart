/**
 * SahanaMart AI Chatbot Floating Widget Controller
 * Week 10 Deliverable: Floating chat widget calling fetch('/api/chat', POST {message})
 */

document.addEventListener("DOMContentLoaded", function () {
    const chatBtn = document.getElementById("chatWidgetBtn");
    const chatPanel = document.getElementById("chatPanel");
    const closeBtn = document.getElementById("chatCloseBtn");
    const chatInput = document.getElementById("chatInput");
    const sendBtn = document.getElementById("chatSendBtn");
    const messagesContainer = document.getElementById("chatMessages");
    const pills = document.querySelectorAll(".chat-pill");

    if (!chatBtn || !chatPanel) return;

    // Toggle chat panel open/close
    chatBtn.addEventListener("click", function () {
        const isHidden = chatPanel.style.display === "none" || chatPanel.style.display === "";
        chatPanel.style.display = isHidden ? "flex" : "none";
        if (isHidden) {
            chatInput.focus();
            scrollToBottom();
        }
    });

    closeBtn.addEventListener("click", function () {
        chatPanel.style.display = "none";
    });

    // Send button event
    sendBtn.addEventListener("click", sendMessage);

    // Enter key event
    chatInput.addEventListener("keypress", function (e) {
        if (e.key === "Enter") {
            sendMessage();
        }
    });

    // Quick-reply pills
    pills.forEach(pill => {
        pill.addEventListener("click", function () {
            const query = this.getAttribute("data-query");
            chatInput.value = query;
            sendMessage();
        });
    });

    async function sendMessage() {
        const text = chatInput.value.trim();
        if (!text) return;

        // Render user message bubble
        appendBubble(text, "chat-bubble-user");
        chatInput.value = "";
        scrollToBottom();

        // Render typing indicator
        const typingIndicator = appendBubble("Thinking...", "chat-bubble-bot");
        scrollToBottom();

        try {
            const response = await fetch("/api/v1/chat", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ message: text })
            });

            const result = await response.json();
            typingIndicator.remove();

            if (result && result.success && result.data) {
                appendBubble(result.data.reply, "chat-bubble-bot");
            } else if (result && result.data && result.data.reply) {
                appendBubble(result.data.reply, "chat-bubble-bot");
            } else {
                appendBubble("I can help you with order tracking, return policies, delivery times, and product categories.", "chat-bubble-bot");
            }
        } catch (err) {
            typingIndicator.remove();
            appendBubble("Sorry, could not connect to assistant. Please email support@sahanamart.com.", "chat-bubble-bot");
        }

        scrollToBottom();
    }

    function appendBubble(text, className) {
        const bubble = document.createElement("div");
        bubble.className = `chat-bubble ${className}`;
        bubble.innerText = text;
        messagesContainer.appendChild(bubble);
        return bubble;
    }

    function scrollToBottom() {
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }
});
