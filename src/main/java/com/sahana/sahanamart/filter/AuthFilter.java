package com.sahana.sahanamart.filter;

import com.sahana.sahanamart.dto.ApiResponse;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.util.JsonUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter enforcing session authentication and Role-Based Access Control (RBAC).
 * Complies with Section 9 & Weeks 1, 4, 7.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Static resources and public routes bypass AuthFilter
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        // Check authentication for protected paths
        if (currentUser == null) {
            handleUnauthorized(req, res, path);
            return;
        }

        // Check Role-Based Access Control
        if (path.startsWith("/seller") || path.startsWith("/api/v1/seller")) {
            if (!currentUser.isSeller() && !currentUser.isAdmin()) {
                handleForbidden(req, res, "Seller access required.");
                return;
            }
        } else if (path.startsWith("/admin") || path.startsWith("/api/v1/admin")) {
            if (!currentUser.isAdmin()) {
                handleForbidden(req, res, "Admin access required.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return path.equals("/") ||
               path.equals("/home") ||
               path.startsWith("/products") ||
               path.startsWith("/auth/") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/") ||
               path.startsWith("/api/v1/auth/") ||
               path.startsWith("/api/v1/products") ||
               path.startsWith("/api/v1/health") ||
               path.startsWith("/api/v1/chat") ||
               path.startsWith("/errors/");
    }

    private void handleUnauthorized(HttpServletRequest req, HttpServletResponse res, String path) throws IOException {
        if (path.startsWith("/api/")) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write(JsonUtil.toJson(ApiResponse.fail("Authentication required. Please log in.")));
        } else {
            String returnUrl = req.getRequestURI();
            if (req.getQueryString() != null) {
                returnUrl += "?" + req.getQueryString();
            }
            res.sendRedirect(req.getContextPath() + "/auth/login?returnUrl=" + java.net.URLEncoder.encode(returnUrl, "UTF-8"));
        }
    }

    private void handleForbidden(HttpServletRequest req, HttpServletResponse res, String message) throws IOException, ServletException {
        if (req.getRequestURI().contains("/api/")) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            res.setContentType("application/json");
            res.getWriter().write(JsonUtil.toJson(ApiResponse.fail("Access forbidden: " + message)));
        } else {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, message);
        }
    }

    @Override
    public void destroy() {
    }
}
