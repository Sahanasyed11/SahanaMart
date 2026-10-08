package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.model.Order;
import com.sahana.sahanamart.model.OrderItem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface OrderDAO {
    Order create(Order order, Connection conn) throws SQLException;
    OrderItem addOrderItem(OrderItem item, Connection conn) throws SQLException;
    Optional<Order> findById(Long id);
    List<Order> findByBuyerId(Long buyerId);
    List<OrderItem> findItemsByOrderId(Long orderId);
    List<OrderItem> findItemsForSeller(Long sellerId);
    List<Order> findAll(int limit, int offset);
    boolean updateStatus(Long orderId, String newStatus);
    long count();
    BigDecimal calculateTotalRevenue();
}
