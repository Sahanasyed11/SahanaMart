package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.model.Review;
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

public class ReviewDAOImpl implements ReviewDAO {
    private static final Logger logger = LoggerFactory.getLogger(ReviewDAOImpl.class);

    private Review mapRow(ResultSet rs) throws SQLException {
        Review r = new Review(
                rs.getLong("id"),
                rs.getLong("product_id"),
                rs.getLong("user_id"),
                rs.getInt("rating"),
                rs.getString("comment"),
                rs.getTimestamp("created_at")
        );
        try {
            r.setUserName(rs.getString("user_name"));
        } catch (SQLException ignored) {
        }
        return r;
    }

    @Override
    public Review create(Review review) {
        String sql = "INSERT INTO reviews (product_id, user_id, rating, comment, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setLong(1, review.getProductId());
            ps.setLong(2, review.getUserId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());
            ps.setTimestamp(5, now);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    review.setId(rs.getLong(1));
                    review.setCreatedAt(now);
                }
            }
            return review;
        } catch (SQLException e) {
            logger.error("Error creating review: {}", e.getMessage(), e);
            throw new RuntimeException("Database error saving review", e);
        }
    }

    @Override
    public List<Review> findByProductId(Long productId) {
        String sql = "SELECT r.id, r.product_id, r.user_id, r.rating, r.comment, r.created_at, u.name AS user_name " +
                     "FROM reviews r " +
                     "INNER JOIN users u ON r.user_id = u.id " +
                     "WHERE r.product_id = ? ORDER BY r.created_at DESC";

        List<Review> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding reviews for product {}: {}", productId, e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean hasPurchasedProduct(Long userId, Long productId) {
        // Check if user has an order with COMPLETED payment or DELIVERED status containing this product
        String sql = "SELECT COUNT(*) FROM order_items oi " +
                     "INNER JOIN orders o ON oi.order_id = o.id " +
                     "WHERE o.buyer_id = ? AND oi.product_id = ?";

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
            logger.error("Error checking purchased product: {}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean hasReviewedProduct(Long userId, Long productId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE user_id = ? AND product_id = ?";
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
            logger.error("Error checking reviewed product: {}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public double getAverageRating(Long productId) {
        String sql = "SELECT COALESCE(AVG(rating), 0.0) FROM reviews WHERE product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error calculating average rating: {}", e.getMessage(), e);
        }
        return 0.0;
    }

    @Override
    public int getReviewCount(Long productId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error counting reviews: {}", e.getMessage(), e);
        }
        return 0;
    }
}
