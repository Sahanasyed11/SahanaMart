package com.sahana.sahanamart.controller;

import com.sahana.sahanamart.dto.ApiResponse;
import com.sahana.sahanamart.model.Order;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.service.OrderService;
import com.sahana.sahanamart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet({"/orders/*", "/api/v1/orders/*"})
public class OrderServlet extends HttpServlet {
    private OrderService orderService;

    @Override
    public void init() {
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

        String uri = req.getRequestURI();
        boolean isApi = uri.contains("/api/v1/");
        String path = req.getPathInfo();
        if (path == null) path = "";

        if (path.equals("/success")) {
            req.getRequestDispatcher("/WEB-INF/views/buyer/order-success.jsp").forward(req, resp);
        } else if (path.equals("/view")) {
            handleOrderDetails(req, resp, user, isApi);
        } else {
            handleOrderList(req, resp, user, isApi);
        }
    }

    private void handleOrderList(HttpServletRequest req, HttpServletResponse resp, User user, boolean isApi) throws IOException, ServletException {
        List<Order> orders = orderService.getBuyerOrders(user.getId());
        if (isApi) {
            resp.setContentType("application/json");
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.ok(orders)));
        } else {
            req.setAttribute("orders", orders);
            req.getRequestDispatcher("/WEB-INF/views/buyer/orders.jsp").forward(req, resp);
        }
    }

    private void handleOrderDetails(HttpServletRequest req, HttpServletResponse resp, User user, boolean isApi) throws IOException, ServletException {
        try {
            Long orderId = Long.parseLong(req.getParameter("id"));
            Order order = orderService.getOrderById(orderId, user.getId(), user.getRole());

            if (isApi) {
                resp.setContentType("application/json");
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.ok(order)));
            } else {
                req.setAttribute("order", order);
                req.getRequestDispatcher("/WEB-INF/views/buyer/order-details.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/orders");
        }
    }
}
