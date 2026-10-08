package com.sahana.sahanamart.controller;

import com.sahana.sahanamart.dto.ApiResponse;
import com.sahana.sahanamart.model.CartItem;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.service.CartService;
import com.sahana.sahanamart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet({"/cart/*", "/api/v1/cart/*"})
public class CartServlet extends HttpServlet {
    private CartService cartService;

    @Override
    public void init() {
        this.cartService = new CartService();
    }

    private Long getUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) return user.getId();
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = getUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String uri = req.getRequestURI();
        boolean isApi = uri.contains("/api/v1/");

        List<CartItem> items = cartService.getCart(userId);
        BigDecimal total = cartService.calculateTotal(items);

        if (isApi) {
            resp.setContentType("application/json");
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.ok(items)));
        } else {
            req.setAttribute("cartItems", items);
            req.setAttribute("cartTotal", total);
            req.getRequestDispatcher("/WEB-INF/views/buyer/cart.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = getUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) action = "add";

        try {
            if ("add".equalsIgnoreCase(action)) {
                Long productId = Long.parseLong(req.getParameter("productId"));
                int quantity = 1;
                if (req.getParameter("quantity") != null) {
                    quantity = Integer.parseInt(req.getParameter("quantity"));
                }
                cartService.addToCart(userId, productId, quantity);
            } else if ("update".equalsIgnoreCase(action)) {
                Long cartItemId = Long.parseLong(req.getParameter("cartItemId"));
                int quantity = Integer.parseInt(req.getParameter("quantity"));
                cartService.updateQuantity(cartItemId, userId, quantity);
            } else if ("remove".equalsIgnoreCase(action)) {
                Long cartItemId = Long.parseLong(req.getParameter("cartItemId"));
                cartService.removeFromCart(cartItemId, userId);
            }

            resp.sendRedirect(req.getContextPath() + "/cart");
        } catch (Exception e) {
            req.getSession().setAttribute("cartError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }
}
