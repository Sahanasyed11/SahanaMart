package com.sahana.sahanamart.controller;

import com.sahana.sahanamart.model.Product;
import com.sahana.sahanamart.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"", "/home"}, loadOnStartup = 1)
public class HomeServlet extends HttpServlet {
    private ProductService productService;

    @Override
    public void init() {
        this.productService = new ProductService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> searchResult = productService.searchProducts("", "ALL", 1, 8);
        @SuppressWarnings("unchecked")
        List<Product> featuredProducts = (List<Product>) searchResult.get("products");
        List<String> categories = productService.getCategories();

        req.setAttribute("featuredProducts", featuredProducts);
        req.setAttribute("categories", categories);
        req.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(req, resp);
    }
}
