package com.sahana.sahanamart.controller;

import com.sahana.sahanamart.dto.OrderRequestDTO;
import com.sahana.sahanamart.model.CartItem;
import com.sahana.sahanamart.model.Order;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.service.CartService;
import com.sahana.sahanamart.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/checkout/*")
public class CheckoutServlet extends HttpServlet {
    private CartService cartService;
    private OrderService orderService;

    @Override
    public void init() {
        this.cartService = new CartService();
        this.orderService = new OrderService();
    }

    private User getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("user") : null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getCurrentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        List<CartItem> cartItems = cartService.getCart(user.getId());
        if (cartItems.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        BigDecimal total = cartService.calculateTotal(cartItems);
        req.setAttribute("cartItems", cartItems);
        req.setAttribute("cartTotal", total);
        req.setAttribute("user", user);

        req.getRequestDispatcher("/WEB-INF/views/buyer/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getCurrentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        try {
            String address = req.getParameter("shippingAddress");
            String paymentMethod = req.getParameter("paymentMethod");

            OrderRequestDTO dto = new OrderRequestDTO(address, paymentMethod);
            Order placedOrder = orderService.checkout(user.getId(), dto);

            // Store order in session for thank-you screen
            req.getSession().setAttribute("latestOrder", placedOrder);
            resp.sendRedirect(req.getContextPath() + "/orders/success?orderId=" + placedOrder.getId());
        } catch (Exception e) {
            req.setAttribute("errorMessage", e.getMessage());
            doGet(req, resp);
        }
    }
}
