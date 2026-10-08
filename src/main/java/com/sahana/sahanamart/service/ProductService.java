package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dao.ProductDAO;
import com.sahana.sahanamart.dao.ProductDAOImpl;
import com.sahana.sahanamart.dto.ProductDTO;
import com.sahana.sahanamart.exception.ResourceNotFoundException;
import com.sahana.sahanamart.exception.UnauthorizedException;
import com.sahana.sahanamart.model.Product;
import com.sahana.sahanamart.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAOImpl();
    }

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public Product createProduct(Long sellerId, ProductDTO dto) {
        ValidationUtil.validateProduct(dto.getName(), dto.getDescription(), dto.getPrice(), dto.getStockQty(), dto.getCategory());

        Product product = new Product();
        product.setSellerId(sellerId);
        product.setName(dto.getName().trim());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQty(dto.getStockQty());
        product.setCategory(dto.getCategory().trim());
        product.setImageUrl(dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty() 
                ? dto.getImageUrl().trim() 
                : "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&q=80");

        return productDAO.create(product);
    }

    public Product getProductById(Long id) {
        return productDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Map<String, Object> searchProducts(String keyword, String category, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 12;
        int offset = (page - 1) * pageSize;

        List<Product> products = productDAO.search(keyword, category, pageSize, offset);
        long totalItems = productDAO.countSearch(keyword, category);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        Map<String, Object> result = new HashMap<>();
        result.put("products", products);
        result.put("currentPage", page);
        result.put("pageSize", pageSize);
        result.put("totalItems", totalItems);
        result.put("totalPages", totalPages);
        result.put("keyword", keyword != null ? keyword : "");
        result.put("category", category != null ? category : "ALL");

        return result;
    }

    public List<Product> getSellerProducts(Long sellerId) {
        return productDAO.findBySellerId(sellerId);
    }

    public List<String> getCategories() {
        return productDAO.findDistinctCategories();
    }

    public Product updateProduct(Long sellerId, ProductDTO dto, boolean isAdmin) {
        ValidationUtil.validateProduct(dto.getName(), dto.getDescription(), dto.getPrice(), dto.getStockQty(), dto.getCategory());

        Product existing = getProductById(dto.getId());
        if (!isAdmin && !existing.getSellerId().equals(sellerId)) {
            throw new UnauthorizedException("You are not authorized to edit this product listing.");
        }

        existing.setName(dto.getName().trim());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setStockQty(dto.getStockQty());
        existing.setCategory(dto.getCategory().trim());
        if (dto.getImageUrl() != null && !dto.getImageUrl().trim().isEmpty()) {
            existing.setImageUrl(dto.getImageUrl().trim());
        }

        productDAO.update(existing);
        return existing;
    }

    public boolean deleteProduct(Long sellerId, Long productId, boolean isAdmin) {
        Product existing = getProductById(productId);
        if (!isAdmin && !existing.getSellerId().equals(sellerId)) {
            throw new UnauthorizedException("You are not authorized to delete this product listing.");
        }
        return productDAO.delete(productId);
    }

    public long getTotalProductCount() {
        return productDAO.count();
    }
}