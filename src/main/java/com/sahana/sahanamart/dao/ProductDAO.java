package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.model.Product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    Product create(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll(int limit, int offset);
    List<Product> search(String keyword, String category, int limit, int offset);
    long countSearch(String keyword, String category);
    List<Product> findBySellerId(Long sellerId);
    List<String> findDistinctCategories();
    boolean update(Product product);
    boolean updateStock(Long productId, int newStock);
    boolean deductStock(Long productId, int quantity, Connection conn) throws SQLException;
    boolean delete(Long id);
    long count();
}
