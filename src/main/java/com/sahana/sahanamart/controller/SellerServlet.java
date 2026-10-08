package com.sahana.sahanamart.controller;

import com.sahana.sahanamart.dto.ProductDTO;
import com.sahana.sahanamart.model.OrderItem;
import com.sahana.sahanamart.model.Product;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.service.OrderService;
import com.sahana.sahanamart.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/seller/*")
public class SellerServlet extends HttpServlet {
    private ProductService productService;
    private OrderService orderService;

    @Override
    public void init() {
        this.productService = new ProductService();
        this.orderService = new OrderService();
    }

    private User getSellerUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null && (user.isSeller() || user.isAdmin())) {
                return user;
            }
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User seller = getSellerUser(req);
        if (seller == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String path = req.getPathInfo();
        if (path == null) path = "/dashboard";

        switch (path) {
            case "/dashboard":
                handleDashboard(req, resp, seller);
                break;
            case "/products":
                handleManageProducts(req, resp, seller);
                break;
            case "/products/new":
                handleProductForm(req, resp, null);
                break;
            case "/products/edit":
                handleEditProductForm(req, resp, seller);
                break;
            case "/orders":
                handleIncomingOrders(req, resp, seller);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
                break;
        }
    }

    private void handleDashboard(HttpServletRequest req, HttpServletResponse resp, User seller) throws ServletException, IOException {
        List<Product> products = productService.getSellerProducts(seller.getId());
        List<OrderItem> incomingItems = orderService.getSellerOrders(seller.getId());

        int totalProducts = products.size();
        int totalUnitsSold = incomingItems.stream().mapToInt(OrderItem::getQuantity).sum();
        BigDecimal totalRevenue = incomingItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        req.setAttribute("products", products);
        req.setAttribute("totalProducts", totalProducts);
        req.setAttribute("totalUnitsSold", totalUnitsSold);
        req.setAttribute("totalRevenue", totalRevenue);
        req.setAttribute("recentOrders", incomingItems.stream().limit(5).toList());

        req.getRequestDispatcher("/WEB-INF/views/seller/dashboard.jsp").forward(req, resp);
    }

    private void handleManageProducts(HttpServletRequest req, HttpServletResponse resp, User seller) throws ServletException, IOException {
        List<Product> products = productService.getSellerProducts(seller.getId());
        req.setAttribute("products", products);
        req.getRequestDispatcher("/WEB-INF/views/seller/manage-products.jsp").forward(req, resp);
    }

    private void handleProductForm(HttpServletRequest req, HttpServletResponse resp, Product product) throws ServletException, IOException {
        req.setAttribute("product", product);
        req.setAttribute("categories", productService.getCategories());
        req.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(req, resp);
    }

    private void handleEditProductForm(HttpServletRequest req, HttpServletResponse resp, User seller) throws ServletException, IOException {
        try {
            Long productId = Long.parseLong(req.getParameter("id"));
            Product product = productService.getProductById(productId);
            if (!product.getSellerId().equals(seller.getId()) && !seller.isAdmin()) {
                resp.sendRedirect(req.getContextPath() + "/seller/products");
                return;
            }
            handleProductForm(req, resp, product);
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/seller/products");
        }
    }

    private void handleIncomingOrders(HttpServletRequest req, HttpServletResponse resp, User seller) throws ServletException, IOException {
        List<OrderItem> incomingItems = orderService.getSellerOrders(seller.getId());
        req.setAttribute("incomingOrders", incomingItems);
        req.getRequestDispatcher("/WEB-INF/views/seller/incoming-orders.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User seller = getSellerUser(req);
        if (seller == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String path = req.getPathInfo();
        if (path == null) path = "";

        if (path.equals("/products/save")) {
            handleSaveProduct(req, resp, seller);
        } else if (path.equals("/products/delete")) {
            handleDeleteProduct(req, resp, seller);
        } else if (path.equals("/orders/status")) {
            handleUpdateOrderStatus(req, resp, seller);
        } else {
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
        }
    }

    private void handleSaveProduct(HttpServletRequest req, HttpServletResponse resp, User seller) throws ServletException, IOException {
        try {
            String idStr = req.getParameter("id");
            ProductDTO dto = new ProductDTO();
            dto.setName(req.getParameter("name"));
            dto.setDescription(req.getParameter("description"));
            dto.setPrice(new BigDecimal(req.getParameter("price")));
            dto.setStockQty(Integer.parseInt(req.getParameter("stockQty")));
            dto.setCategory(req.getParameter("category"));
            dto.setImageUrl(req.getParameter("imageUrl"));

            if (idStr != null && !idStr.trim().isEmpty()) {
                dto.setId(Long.parseLong(idStr));
                productService.updateProduct(seller.getId(), dto, seller.isAdmin());
            } else {
                productService.createProduct(seller.getId(), dto);
            }

            resp.sendRedirect(req.getContextPath() + "/seller/products?saved=true");
        } catch (Exception e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(req, resp);
        }
    }

    private void handleDeleteProduct(HttpServletRequest req, HttpServletResponse resp, User seller) throws IOException {
        try {
            Long productId = Long.parseLong(req.getParameter("id"));
            productService.deleteProduct(seller.getId(), productId, seller.isAdmin());
        } catch (Exception ignored) {
        }
        resp.sendRedirect(req.getContextPath() + "/seller/products?deleted=true");
    }

    private void handleUpdateOrderStatus(HttpServletRequest req, HttpServletResponse resp, User seller) throws IOException {
        try {
            Long orderId = Long.parseLong(req.getParameter("orderId"));
            String status = req.getParameter("status");
            orderService.updateOrderStatus(orderId, status);
        } catch (Exception ignored) {
        }
        resp.sendRedirect(req.getContextPath() + "/seller/orders?updated=true");
    }
}
