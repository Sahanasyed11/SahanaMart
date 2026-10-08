package com.sahana.sahanamart.controller;

import com.sahana.sahanamart.dto.ApiResponse;
import com.sahana.sahanamart.dto.ProductDTO;
import com.sahana.sahanamart.model.Product;
import com.sahana.sahanamart.model.Review;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.service.ProductService;
import com.sahana.sahanamart.service.ReviewService;
import com.sahana.sahanamart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet({"/products/*", "/api/v1/products/*"})
public class ProductServlet extends HttpServlet {
    private ProductService productService;
    private ReviewService reviewService;

    @Override
    public void init() {
        this.productService = new ProductService();
        this.reviewService = new ReviewService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        boolean isApi = uri.contains("/api/v1/");
        String path = req.getPathInfo();
        if (path == null) path = "";

        if (isApi) {
            handleApiGet(req, resp, path);
            return;
        }

        // HTML Views
        if (path.equals("/view")) {
            handleProductDetails(req, resp);
        } else {
            handleProductCatalog(req, resp);
        }
    }

    private void handleProductCatalog(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        String category = req.getParameter("category");
        int page = 1;
        try {
            if (req.getParameter("page") != null) {
                page = Integer.parseInt(req.getParameter("page"));
            }
        } catch (NumberFormatException ignored) {
        }

        Map<String, Object> result = productService.searchProducts(keyword, category, page, 9);
        List<String> categories = productService.getCategories();

        req.setAttribute("products", result.get("products"));
        req.setAttribute("currentPage", result.get("currentPage"));
        req.setAttribute("pageSize", result.get("pageSize"));
        req.setAttribute("totalItems", result.get("totalItems"));
        req.setAttribute("totalPages", result.get("totalPages"));
        req.setAttribute("keyword", result.get("keyword"));
        req.setAttribute("selectedCategory", result.get("category"));
        req.setAttribute("categories", categories);

        req.getRequestDispatcher("/WEB-INF/views/buyer/catalog.jsp").forward(req, resp);
    }

    private void handleProductDetails(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            Product product = productService.getProductById(id);
            List<Review> reviews = reviewService.getProductReviews(id);

            HttpSession session = req.getSession(false);
            User user = (session != null) ? (User) session.getAttribute("user") : null;
            boolean canReview = false;
            if (user != null && user.isBuyer()) {
                canReview = reviewService.canUserReview(user.getId(), id);
            }

            req.setAttribute("product", product);
            req.setAttribute("reviews", reviews);
            req.setAttribute("canReview", canReview);
            req.getRequestDispatcher("/WEB-INF/views/buyer/product-details.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/products");
        }
    }

    private void handleApiGet(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.setContentType("application/json");
        try {
            if (path.length() > 1 && !path.equals("/")) {
                Long id = Long.parseLong(path.substring(1));
                Product p = productService.getProductById(id);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.ok(p)));
            } else {
                String keyword = req.getParameter("keyword");
                String category = req.getParameter("category");
                int page = 1;
                if (req.getParameter("page") != null) {
                    page = Integer.parseInt(req.getParameter("page"));
                }
                Map<String, Object> result = productService.searchProducts(keyword, category, page, 20);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.ok(result)));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.fail(e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        if (uri.contains("/api/v1/")) {
            resp.setContentType("application/json");
            try {
                HttpSession session = req.getSession(false);
                User user = (session != null) ? (User) session.getAttribute("user") : null;
                if (user == null || (!user.isSeller() && !user.isAdmin())) {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    resp.getWriter().write(JsonUtil.toJson(ApiResponse.fail("Seller privileges required")));
                    return;
                }

                ProductDTO dto = JsonUtil.fromJson(req.getReader(), ProductDTO.class);
                Product created = productService.createProduct(user.getId(), dto);
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.ok(created)));
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.fail(e.getMessage())));
            }
        } else {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();
        if (uri.contains("/api/v1/")) {
            resp.setContentType("application/json");
            try {
                HttpSession session = req.getSession(false);
                User user = (session != null) ? (User) session.getAttribute("user") : null;
                if (user == null || (!user.isSeller() && !user.isAdmin())) {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    resp.getWriter().write(JsonUtil.toJson(ApiResponse.fail("Seller privileges required")));
                    return;
                }

                String path = req.getPathInfo();
                Long id = Long.parseLong(path.substring(1));
                boolean deleted = productService.deleteProduct(user.getId(), id, user.isAdmin());
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.ok(deleted)));
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.fail(e.getMessage())));
            }
        }
    }
}
