package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.model.Product;
import com.sahana.sahanamart.model.WishlistItem;
import com.sahana.sahanamart.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAOImpl implements WishlistDAO {
    private static final Logger logger = LoggerFactory.getLogger(WishlistDAOImpl.class);

    @Override
    public boolean addToWishlist(Long userId, Long productId) {
        if (isInWishlist(userId, productId)) {
            return true;
        }
        String sql = "INSERT INTO wishlist_items (user_id, product_id, created_at) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, productId);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error adding to wishlist: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean removeFromWishlist(Long userId, Long productId) {
        String sql = "DELETE FROM wishlist_items WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, productId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error removing from wishlist: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean isInWishlist(Long userId, Long productId) {
        String sql = "SELECT COUNT(*) FROM wishlist_items WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, productId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking wishlist: {}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public List<WishlistItem> findByUserId(Long userId) {
        String sql = "SELECT w.id, w.user_id, w.product_id, w.created_at, " +
                     "p.id AS p_id, p.name AS p_name, p.description AS p_description, " +
                     "p.price AS p_price, p.stock_qty AS p_stock, p.category AS p_category, " +
                     "p.image_url AS p_image " +
                     "FROM wishlist_items w " +
                     "INNER JOIN products p ON w.product_id = p.id " +
                     "WHERE w.user_id = ? ORDER BY w.id DESC";

        List<WishlistItem> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WishlistItem item = new WishlistItem(
                            rs.getLong("id"),
                            rs.getLong("user_id"),
                            rs.getLong("product_id"),
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
                    item.setProduct(p);

                    list.add(item);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching wishlist for user {}: {}", userId, e.getMessage(), e);
        }
        return list;
    }
}
