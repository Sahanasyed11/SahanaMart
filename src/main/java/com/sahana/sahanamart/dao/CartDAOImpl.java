package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.model.CartItem;
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

public class CartDAOImpl implements CartDAO {
    private static final Logger logger = LoggerFactory.getLogger(CartDAOImpl.class);

    private CartItem mapRowWithProduct(ResultSet rs) throws SQLException {
        CartItem item = new CartItem(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("product_id"),
                rs.getInt("quantity"),
                rs.getTimestamp("created_at")
        );

        Product p = new Product();
        p.setId(rs.getLong("p_id"));
        p.setName(rs.getString("p_name"));
        p.setDescription(rs.getString("p_description"));
        p.setPrice(rs.getBigDecimal("p_price"));
        p.setStockQty(rs.getInt("p_stock"));
        p.setCategory(rs.getString("p_category"));
        p.setImageUrl(rs.getString("p_image"));
        p.setSellerId(rs.getLong("p_seller_id"));
        item.setProduct(p);

        return item;
    }

    @Override
    public CartItem addToCart(Long userId, Long productId, int quantity) {
        // Check if item already exists in cart; if so, update quantity
        Optional<CartItem> existing = findByUserAndProduct(userId, productId);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            int newQty = item.getQuantity() + quantity;
            updateQuantity(item.getId(), userId, newQty);
            item.setQuantity(newQty);
            return item;
        }

        String sql = "INSERT INTO cart_items (user_id, product_id, quantity, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            ps.setInt(3, quantity);
            ps.setTimestamp(4, now);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new CartItem(rs.getLong(1), userId, productId, quantity, now);
                }
            }
        } catch (SQLException e) {
            logger.error("Error adding to cart: {}", e.getMessage(), e);
            throw new RuntimeException("Database error adding to cart", e);
        }
        return null;
    }

    @Override
    public boolean updateQuantity(Long cartItemId, Long userId, int quantity) {
        String sql = "UPDATE cart_items SET quantity = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setLong(2, cartItemId);
            ps.setLong(3, userId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating cart quantity: {}", e.getMessage(), e);
            throw new RuntimeException("Database error updating cart", e);
        }
    }

    @Override
    public boolean removeItem(Long cartItemId, Long userId) {
        String sql = "DELETE FROM cart_items WHERE id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cartItemId);
            ps.setLong(2, userId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error removing item from cart: {}", e.getMessage(), e);
            throw new RuntimeException("Database error removing cart item", e);
        }
    }

    @Override
    public boolean clearCart(Long userId) {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) {
            logger.error("Error clearing cart: {}", e.getMessage(), e);
            throw new RuntimeException("Database error clearing cart", e);
        }
    }

    @Override
    public boolean clearCart(Long userId, Connection conn) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            return ps.executeUpdate() >= 0;
        }
    }

    @Override
    public List<CartItem> findByUserId(Long userId) {
        String sql = "SELECT c.id, c.user_id, c.product_id, c.quantity, c.created_at, " +
                     "p.id AS p_id, p.name AS p_name, p.description AS p_description, " +
                     "p.price AS p_price, p.stock_qty AS p_stock, p.category AS p_category, " +
                     "p.image_url AS p_image, p.seller_id AS p_seller_id " +
                     "FROM cart_items c " +
                     "INNER JOIN products p ON c.product_id = p.id " +
                     "WHERE c.user_id = ? ORDER BY c.id DESC";

        List<CartItem> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowWithProduct(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding cart items for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Database error retrieving cart", e);
        }
        return list;
    }

    @Override
    public Optional<CartItem> findByUserAndProduct(Long userId, Long productId) {
        String sql = "SELECT c.id, c.user_id, c.product_id, c.quantity, c.created_at, " +
                     "p.id AS p_id, p.name AS p_name, p.description AS p_description, " +
                     "p.price AS p_price, p.stock_qty AS p_stock, p.category AS p_category, " +
                     "p.image_url AS p_image, p.seller_id AS p_seller_id " +
                     "FROM cart_items c " +
                     "INNER JOIN products p ON c.product_id = p.id " +
                     "WHERE c.user_id = ? AND c.product_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowWithProduct(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking cart product: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public int countItems(Long userId) {
        String sql = "SELECT COALESCE(SUM(quantity), 0) FROM cart_items WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error counting cart items: {}", e.getMessage(), e);
        }
        return 0;
    }
}
