package com.sahana.sahanamart.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.sahana.sahanamart.dto.ApiResponse;
import com.sahana.sahanamart.dto.ChatRequestDTO;
import com.sahana.sahanamart.dto.ChatResponseDTO;
import com.sahana.sahanamart.service.ChatService;
import com.sahana.sahanamart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet({"/api/v1/chat", "/api/chat"})
public class ChatServlet extends HttpServlet {
    private ChatService chatService;

    @Override
    public void init() {
        this.chatService = new ChatService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(true);
        String sessionId = session.getId();

        try {
            String message = null;
            String contentType = req.getContentType();

            if (contentType != null && contentType.toLowerCase().contains("application/json")) {
                JsonNode root = JsonUtil.getMapper().readTree(req.getReader());
                if (root.has("message")) {
                    message = root.get("message").asText();
                }
            } else {
                message = req.getParameter("message");
            }

            ChatResponseDTO reply = chatService.processChat(sessionId, message);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.ok(reply)));

        } catch (Exception e) {
            ChatResponseDTO fallback = new ChatResponseDTO(
                    "Hello! How can I assist you with SahanaMart products, order tracking, or return policies today?",
                    "Fallback", false
            );
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.ok(fallback)));
        }
    }
}
