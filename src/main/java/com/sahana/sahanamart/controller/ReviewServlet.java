package com.sahana.sahanamart.controller;

import com.sahana.sahanamart.dto.ApiResponse;
import com.sahana.sahanamart.model.Review;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.service.ReviewService;
import com.sahana.sahanamart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet({"/reviews/*", "/api/v1/reviews/*"})
public class ReviewServlet extends HttpServlet {
    private ReviewService reviewService;

    @Override
    public void init() {
        this.reviewService = new ReviewService();
    }

    private User getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("user") : null;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getCurrentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String uri = req.getRequestURI();
        boolean isApi = uri.contains("/api/v1/");

        try {
            Long productId = Long.parseLong(req.getParameter("productId"));
            int rating = Integer.parseInt(req.getParameter("rating"));
            String comment = req.getParameter("comment");

            Review review = reviewService.addReview(user.getId(), productId, rating, comment);

            if (isApi) {
                resp.setContentType("application/json");
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.ok(review)));
            } else {
                resp.sendRedirect(req.getContextPath() + "/products/view?id=" + productId + "&reviewed=true");
            }
        } catch (Exception e) {
            if (isApi) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.fail(e.getMessage())));
            } else {
                String productId = req.getParameter("productId");
                req.getSession().setAttribute("reviewError", e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/products/view?id=" + productId);
            }
        }
    }
}
