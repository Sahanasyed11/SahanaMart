package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.model.Product;
import com.sahana.sahanamart.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAOImpl implements ProductDAO {
    private static final Logger logger = LoggerFactory.getLogger(ProductDAOImpl.class);

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product(
                rs.getLong("id"),
                rs.getLong("seller_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBigDecimal("price"),
                rs.getInt("stock_qty"),
                rs.getString("category"),
                rs.getString("image_url"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at")
        );
        try {
            p.setSellerName(rs.getString("seller_name"));
        } catch (SQLException ignored) {
        }
        try {
            p.setAverageRating(rs.getDouble("avg_rating"));
            p.setReviewCount(rs.getInt("review_count"));
        } catch (SQLException ignored) {
        }
        return p;
    }

    private static final String BASE_SELECT =
            "SELECT p.id, p.seller_id, p.name, p.description, p.price, p.stock_qty, p.category, p.image_url, " +
            "p.created_at, p.updated_at, u.name AS seller_name, " +
            "COALESCE(AVG(r.rating), 0.0) AS avg_rating, COUNT(r.id) AS review_count " +
            "FROM products p " +
            "LEFT JOIN users u ON p.seller_id = u.id " +
            "LEFT JOIN reviews r ON p.id = r.product_id ";

    @Override
    public Product create(Product product) {
        String sql = "INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setLong(1, product.getSellerId());
            ps.setString(2, product.getName());
            ps.setString(3, product.getDescription());
            ps.setBigDecimal(4, product.getPrice());
            ps.setInt(5, product.getStockQty());
            ps.setString(6, product.getCategory());
            ps.setString(7, product.getImageUrl());
            ps.setTimestamp(8, now);
            ps.setTimestamp(9, now);

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Failed to create product, no rows affected.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    product.setId(rs.getLong(1));
                    product.setCreatedAt(now);
                    product.setUpdatedAt(now);
                }
            }
            return product;
        } catch (SQLException e) {
            logger.error("Error creating product: {}", e.getMessage(), e);
            throw new RuntimeException("Database error creating product", e);
        }
    }

    @Override
    public Optional<Product> findById(Long id) {
        String sql = BASE_SELECT + "WHERE p.id = ? GROUP BY p.id, u.name";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding product by id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Database error finding product", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Product> findAll(int limit, int offset) {
        String sql = BASE_SELECT + "GROUP BY p.id, u.name ORDER BY p.id DESC LIMIT ? OFFSET ?";
        List<Product> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding all products: {}", e.getMessage(), e);
            throw new RuntimeException("Database error fetching products", e);
        }
        return list;
    }

    @Override
    public List<Product> search(String keyword, String category, int limit, int offset) {
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasCategory = category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("ALL");

        List<Object> params = new ArrayList<>();
        if (hasKeyword || hasCategory) {
            sql.append("WHERE ");
            if (hasKeyword) {
                sql.append("(LOWER(p.name) LIKE ? OR LOWER(p.description) LIKE ?) ");
                String kwParam = "%" + keyword.trim().toLowerCase() + "%";
                params.add(kwParam);
                params.add(kwParam);
            }
            if (hasCategory) {
                if (hasKeyword) {
                    sql.append("AND ");
                }
                sql.append("LOWER(p.category) = ? ");
                params.add(category.trim().toLowerCase());
            }
        }

        sql.append("GROUP BY p.id, u.name ORDER BY p.id DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        List<Product> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error searching products: {}", e.getMessage(), e);
            throw new RuntimeException("Database error searching products", e);
        }
        return list;
    }

    @Override
    public long countSearch(String keyword, String category) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM products p ");
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasCategory = category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("ALL");

        List<Object> params = new ArrayList<>();
        if (hasKeyword || hasCategory) {
            sql.append("WHERE ");
            if (hasKeyword) {
                sql.append("(LOWER(p.name) LIKE ? OR LOWER(p.description) LIKE ?) ");
                String kwParam = "%" + keyword.trim().toLowerCase() + "%";
                params.add(kwParam);
                params.add(kwParam);
            }
            if (hasCategory) {
                if (hasKeyword) {
                    sql.append("AND ");
                }
                sql.append("LOWER(p.category) = ? ");
                params.add(category.trim().toLowerCase());
            }
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error counting search products: {}", e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public List<Product> findBySellerId(Long sellerId) {
        String sql = BASE_SELECT + "WHERE p.seller_id = ? GROUP BY p.id, u.name ORDER BY p.id DESC";
        List<Product> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding products for seller {}: {}", sellerId, e.getMessage(), e);
            throw new RuntimeException("Database error fetching seller products", e);
        }
        return list;
    }

    @Override
    public List<String> findDistinctCategories() {
        String sql = "SELECT DISTINCT category FROM products ORDER BY category ASC";
        List<String> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            logger.error("Error fetching distinct categories: {}", e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean update(Product product) {
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, stock_qty = ?, category = ?, image_url = ?, updated_at = ? " +
                     "WHERE id = ? AND seller_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStockQty());
            ps.setString(5, product.getCategory());
            ps.setString(6, product.getImageUrl());
            ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            ps.setLong(8, product.getId());
            ps.setLong(9, product.getSellerId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating product {}: {}", product.getId(), e.getMessage(), e);
            throw new RuntimeException("Database error updating product", e);
        }
    }

    @Override
    public boolean updateStock(Long productId, int newStock) {
        String sql = "UPDATE products SET stock_qty = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newStock);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setLong(3, productId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating stock for product {}: {}", productId, e.getMessage(), e);
            throw new RuntimeException("Database error updating stock", e);
        }
    }

    @Override
    public boolean deductStock(Long productId, int quantity, Connection conn) throws SQLException {
        // Enforce atomic stock reduction ensuring stock doesn't go below 0
        String sql = "UPDATE products SET stock_qty = stock_qty - ?, updated_at = ? WHERE id = ? AND stock_qty >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setLong(3, productId);
            ps.setInt(4, quantity);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting product {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Database error deleting product", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM products";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting products: {}", e.getMessage(), e);
        }
        return 0;
    }
}
