package com.sahana.sahanamart.controller;

import com.sahana.sahanamart.dto.UserResponseDTO;
import com.sahana.sahanamart.model.Order;
import com.sahana.sahanamart.model.Product;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.service.OrderService;
import com.sahana.sahanamart.service.ProductService;
import com.sahana.sahanamart.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {
    private UserService userService;
    private ProductService productService;
    private OrderService orderService;

    @Override
    public void init() {
        this.userService = new UserService();
        this.productService = new ProductService();
        this.orderService = new OrderService();
    }

    private User getAdminUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null && user.isAdmin()) {
                return user;
            }
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = getAdminUser(req);
        if (admin == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String path = req.getPathInfo();
        if (path == null) path = "/dashboard";

        switch (path) {
            case "/dashboard":
                handleDashboard(req, resp);
                break;
            case "/users":
                handleUsers(req, resp);
                break;
            case "/orders":
                handleOrders(req, resp);
                break;
            case "/products":
                handleProducts(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
                break;
        }
    }

    private void handleDashboard(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long totalUsers = userService.getTotalUserCount();
        long totalProducts = productService.getTotalProductCount();
        long totalOrders = orderService.getTotalOrderCount();
        BigDecimal totalRevenue = orderService.getTotalSalesRevenue();
        List<Order> recentOrders = orderService.getAllOrders(1, 5);

        req.setAttribute("totalUsers", totalUsers);
        req.setAttribute("totalProducts", totalProducts);
        req.setAttribute("totalOrders", totalOrders);
        req.setAttribute("totalRevenue", totalRevenue);
        req.setAttribute("recentOrders", recentOrders);

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }

    private void handleUsers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<UserResponseDTO> users = userService.getAllUsers();
        req.setAttribute("users", users);
        req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);
    }

    private void handleOrders(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Order> orders = orderService.getAllOrders(1, 50);
        req.setAttribute("orders", orders);
        req.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp").forward(req, resp);
    }

    private void handleProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> result = productService.searchProducts("", "ALL", 1, 50);
        req.setAttribute("products", result.get("products"));
        req.getRequestDispatcher("/WEB-INF/views/admin/products.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = getAdminUser(req);
        if (admin == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String path = req.getPathInfo();
        if (path == null) path = "";

        if (path.equals("/users/delete")) {
            Long userId = Long.parseLong(req.getParameter("id"));
            userService.deleteUser(userId);
            resp.sendRedirect(req.getContextPath() + "/admin/users?deleted=true");
        } else if (path.equals("/products/delete")) {
            Long productId = Long.parseLong(req.getParameter("id"));
            productService.deleteProduct(admin.getId(), productId, true);
            resp.sendRedirect(req.getContextPath() + "/admin/products?deleted=true");
        } else if (path.equals("/orders/status")) {
            Long orderId = Long.parseLong(req.getParameter("orderId"));
            String status = req.getParameter("status");
            orderService.updateOrderStatus(orderId, status);
            resp.sendRedirect(req.getContextPath() + "/admin/orders?updated=true");
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        }
    }
}
