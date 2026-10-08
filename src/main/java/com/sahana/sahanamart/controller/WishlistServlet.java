package com.sahana.sahanamart.controller;

import com.sahana.sahanamart.dto.ApiResponse;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.model.WishlistItem;
import com.sahana.sahanamart.service.WishlistService;
import com.sahana.sahanamart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet({"/wishlist/*", "/api/v1/wishlist/*"})
public class WishlistServlet extends HttpServlet {
    private WishlistService wishlistService;

    @Override
    public void init() {
        this.wishlistService = new WishlistService();
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

        List<WishlistItem> items = wishlistService.getWishlist(user.getId());
        req.setAttribute("wishlistItems", items);
        req.getRequestDispatcher("/WEB-INF/views/buyer/wishlist.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = getCurrentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        String action = req.getParameter("action");
        Long productId = Long.parseLong(req.getParameter("productId"));

        if ("add".equalsIgnoreCase(action)) {
            wishlistService.addToWishlist(user.getId(), productId);
        } else if ("remove".equalsIgnoreCase(action)) {
            wishlistService.removeFromWishlist(user.getId(), productId);
        } else if ("moveToCart".equalsIgnoreCase(action)) {
            wishlistService.moveToCart(user.getId(), productId);
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/wishlist");
    }
}
