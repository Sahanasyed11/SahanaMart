package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.model.Order;
import com.sahana.sahanamart.model.OrderItem;
import com.sahana.sahanamart.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDAOImpl implements OrderDAO {
    private static final Logger logger = LoggerFactory.getLogger(OrderDAOImpl.class);

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order(
                rs.getLong("id"),
                rs.getLong("buyer_id"),
                rs.getBigDecimal("total_amount"),
                rs.getString("status"),
                rs.getString("shipping_address"),
                rs.getString("payment_method"),
                rs.getString("payment_status"),
                rs.getTimestamp("created_at")
        );
        try {
            o.setBuyerName(rs.getString("buyer_name"));
            o.setBuyerEmail(rs.getString("buyer_email"));
        } catch (SQLException ignored) {
        }
        return o;
    }

    private OrderItem mapOrderItem(ResultSet rs) throws SQLException {
        OrderItem item = new OrderItem(
                rs.getLong("id"),
                rs.getLong("order_id"),
                rs.getLong("product_id"),
                rs.getInt("quantity"),
                rs.getBigDecimal("unit_price"),
                rs.getTimestamp("created_at")
        );
        try {
            item.setProductName(rs.getString("product_name"));
            item.setProductImage(rs.getString("product_image"));
            item.setSellerId(rs.getLong("seller_id"));
        } catch (SQLException ignored) {
        }
        return item;
    }

    @Override
    public Order create(Order order, Connection conn) throws SQLException {
        String sql = "INSERT INTO orders (buyer_id, total_amount, status, shipping_address, payment_method, payment_status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setLong(1, order.getBuyerId());
            ps.setBigDecimal(2, order.getTotalAmount());
            ps.setString(3, order.getStatus() != null ? order.getStatus() : "PENDING");
            ps.setString(4, order.getShippingAddress());
            ps.setString(5, order.getPaymentMethod() != null ? order.getPaymentMethod() : "MOCK_CARD");
            ps.setString(6, order.getPaymentStatus() != null ? order.getPaymentStatus() : "COMPLETED");
            ps.setTimestamp(7, now);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    order.setId(rs.getLong(1));
                    order.setCreatedAt(now);
                }
            }
            return order;
        }
    }

    @Override
    public OrderItem addOrderItem(OrderItem item, Connection conn) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price, created_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setLong(1, item.getOrderId());
            ps.setLong(2, item.getProductId());
            ps.setInt(3, item.getQuantity());
            ps.setBigDecimal(4, item.getUnitPrice());
            ps.setTimestamp(5, now);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setId(rs.getLong(1));
                    item.setCreatedAt(now);
                }
            }
            return item;
        }
    }

    @Override
    public Optional<Order> findById(Long id) {
        String sql = "SELECT o.id, o.buyer_id, o.total_amount, o.status, o.shipping_address, o.payment_method, " +
                     "o.payment_status, o.created_at, u.name AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o " +
                     "INNER JOIN users u ON o.buyer_id = u.id " +
                     "WHERE o.id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapOrder(rs);
                    order.setItems(findItemsByOrderId(order.getId()));
                    return Optional.of(order);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding order by id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Database error finding order", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Order> findByBuyerId(Long buyerId) {
        String sql = "SELECT o.id, o.buyer_id, o.total_amount, o.status, o.shipping_address, o.payment_method, " +
                     "o.payment_status, o.created_at, u.name AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o " +
                     "INNER JOIN users u ON o.buyer_id = u.id " +
                     "WHERE o.buyer_id = ? ORDER BY o.id DESC";

        List<Order> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = mapOrder(rs);
                    o.setItems(findItemsByOrderId(o.getId()));
                    list.add(o);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding orders for buyer {}: {}", buyerId, e.getMessage(), e);
            throw new RuntimeException("Database error finding orders", e);
        }
        return list;
    }

    @Override
    public List<OrderItem> findItemsByOrderId(Long orderId) {
        String sql = "SELECT oi.id, oi.order_id, oi.product_id, oi.quantity, oi.unit_price, oi.created_at, " +
                     "p.name AS product_name, p.image_url AS product_image, p.seller_id " +
                     "FROM order_items oi " +
                     "INNER JOIN products p ON oi.product_id = p.id " +
                     "WHERE oi.order_id = ? ORDER BY oi.id ASC";

        List<OrderItem> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapOrderItem(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding items for order {}: {}", orderId, e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<OrderItem> findItemsForSeller(Long sellerId) {
        String sql = "SELECT oi.id, oi.order_id, oi.product_id, oi.quantity, oi.unit_price, oi.created_at, " +
                     "p.name AS product_name, p.image_url AS product_image, p.seller_id " +
                     "FROM order_items oi " +
                     "INNER JOIN products p ON oi.product_id = p.id " +
                     "WHERE p.seller_id = ? ORDER BY oi.id DESC";

        List<OrderItem> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapOrderItem(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding order items for seller {}: {}", sellerId, e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Order> findAll(int limit, int offset) {
        String sql = "SELECT o.id, o.buyer_id, o.total_amount, o.status, o.shipping_address, o.payment_method, " +
                     "o.payment_status, o.created_at, u.name AS buyer_name, u.email AS buyer_email " +
                     "FROM orders o " +
                     "INNER JOIN users u ON o.buyer_id = u.id " +
                     "ORDER BY o.id DESC LIMIT ? OFFSET ?";

        List<Order> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = mapOrder(rs);
                    o.setItems(findItemsByOrderId(o.getId()));
                    list.add(o);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding all orders: {}", e.getMessage(), e);
            throw new RuntimeException("Database error finding all orders", e);
        }
        return list;
    }

    @Override
    public boolean updateStatus(Long orderId, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus.toUpperCase());
            ps.setLong(2, orderId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating order status for {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("Database error updating order status", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM orders";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting orders: {}", e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public BigDecimal calculateTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0.00) FROM orders WHERE status != 'CANCELLED'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            logger.error("Error calculating total revenue: {}", e.getMessage(), e);
        }
        return BigDecimal.ZERO;
    }
}
