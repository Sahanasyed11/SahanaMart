package com.sahana.sahanamart.controller;

import com.sahana.sahanamart.util.DBUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Health check endpoint required by Week 8 deploy specification:
 * GET /api/v1/health returns {"status":"UP","db":"UP"}
 */
@WebServlet("/api/v1/health")
public class HealthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        boolean dbOk = DBUtil.isHealthy();
        String json = String.format("{\"status\":\"%s\",\"db\":\"%s\"}",
                dbOk ? "UP" : "DOWN",
                dbOk ? "UP" : "DOWN");

        if (!dbOk) {
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } else {
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        resp.getWriter().write(json);
    }
}
