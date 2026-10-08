package com.sahana.sahanamart.controller;

import com.sahana.sahanamart.dto.ApiResponse;
import com.sahana.sahanamart.dto.LoginRequestDTO;
import com.sahana.sahanamart.dto.RegisterRequestDTO;
import com.sahana.sahanamart.dto.UserResponseDTO;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.service.UserService;
import com.sahana.sahanamart.util.ConfigUtil;
import com.sahana.sahanamart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet handling Authentication (F1) for both HTML views and JSON APIs.
 * Regenerates session ID upon login and sets explicit session timeout.
 */
@WebServlet({"/auth/*", "/api/v1/auth/*"})
public class AuthServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "";

        if (path.equals("/login")) {
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        } else if (path.equals("/register")) {
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        } else if (path.equals("/logout")) {
            handleLogout(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        boolean isApi = uri.contains("/api/v1/");
        String path = req.getPathInfo();
        if (path == null) path = "";

        if (path.equals("/login")) {
            handleLogin(req, resp, isApi);
        } else if (path.equals("/register")) {
            handleRegister(req, resp, isApi);
        } else if (path.equals("/logout")) {
            handleLogout(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp, boolean isApi) throws IOException, ServletException {
        try {
            LoginRequestDTO dto;
            if (isApi || "application/json".equalsIgnoreCase(req.getContentType())) {
                dto = JsonUtil.fromJson(req.getReader(), LoginRequestDTO.class);
            } else {
                dto = new LoginRequestDTO(req.getParameter("email"), req.getParameter("password"));
            }

            User user = userService.login(dto);

            // Security requirement: Regenerate session ID on login to protect against session fixation
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession newSession = req.getSession(true);
            int timeoutMinutes = ConfigUtil.getInt("session.timeoutMinutes", 30);
            newSession.setMaxInactiveInterval(timeoutMinutes * 60);
            newSession.setAttribute("user", user);
            newSession.setAttribute("userId", user.getId());
            newSession.setAttribute("userName", user.getName());
            newSession.setAttribute("userRole", user.getRole());

            if (isApi) {
                resp.setContentType("application/json");
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.ok(UserResponseDTO.fromEntity(user))));
            } else {
                String returnUrl = req.getParameter("returnUrl");
                if (returnUrl != null && !returnUrl.trim().isEmpty() && !returnUrl.contains("login")) {
                    resp.sendRedirect(returnUrl);
                } else if (user.isSeller()) {
                    resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
                } else if (user.isAdmin()) {
                    resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/products");
                }
            }
        } catch (Exception e) {
            if (isApi) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.setContentType("application/json");
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.fail(e.getMessage())));
            } else {
                req.setAttribute("errorMessage", e.getMessage());
                req.setAttribute("email", req.getParameter("email"));
                req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            }
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp, boolean isApi) throws IOException, ServletException {
        try {
            RegisterRequestDTO dto;
            if (isApi || "application/json".equalsIgnoreCase(req.getContentType())) {
                dto = JsonUtil.fromJson(req.getReader(), RegisterRequestDTO.class);
            } else {
                dto = new RegisterRequestDTO(
                        req.getParameter("name"),
                        req.getParameter("email"),
                        req.getParameter("password"),
                        req.getParameter("role"),
                        req.getParameter("phone"),
                        req.getParameter("address")
                );
            }

            UserResponseDTO createdUser = userService.register(dto);

            if (isApi) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.setContentType("application/json");
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.ok(createdUser)));
            } else {
                req.setAttribute("successMessage", "Account created successfully! Please log in.");
                req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            }
        } catch (ValidationException e) {
            if (isApi) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.fail(e.getMessage())));
            } else {
                req.setAttribute("errorMessage", e.getMessage());
                req.setAttribute("fieldErrors", e.getFieldErrors());
                req.setAttribute("name", req.getParameter("name"));
                req.setAttribute("email", req.getParameter("email"));
                req.setAttribute("role", req.getParameter("role"));
                req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            if (isApi) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.setContentType("application/json");
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.fail(e.getMessage())));
            } else {
                req.setAttribute("errorMessage", e.getMessage());
                req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            }
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/auth/login?loggedOut=true");
    }
}
