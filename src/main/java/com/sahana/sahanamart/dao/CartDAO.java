package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.model.CartItem;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CartDAO {
    CartItem addToCart(Long userId, Long productId, int quantity);
    boolean updateQuantity(Long cartItemId, Long userId, int quantity);
    boolean removeItem(Long cartItemId, Long userId);
    boolean clearCart(Long userId);
    boolean clearCart(Long userId, Connection conn) throws SQLException;
    List<CartItem> findByUserId(Long userId);
    Optional<CartItem> findByUserAndProduct(Long userId, Long productId);
    int countItems(Long userId);
}
