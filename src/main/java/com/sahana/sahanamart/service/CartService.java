package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dao.CartDAO;
import com.sahana.sahanamart.dao.CartDAOImpl;
import com.sahana.sahanamart.dao.ProductDAO;
import com.sahana.sahanamart.dao.ProductDAOImpl;
import com.sahana.sahanamart.exception.ResourceNotFoundException;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.CartItem;
import com.sahana.sahanamart.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

public class CartService {
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);
    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartService() {
        this.cartDAO = new CartDAOImpl();
        this.productDAO = new ProductDAOImpl();
    }

    public CartService(CartDAO cartDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    public CartItem addToCart(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be at least 1");
        }

        Product product = productDAO.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (product.getStockQty() < quantity) {
            throw new ValidationException("Requested quantity (" + quantity + ") exceeds available stock (" + product.getStockQty() + ")");
        }

        return cartDAO.addToCart(userId, productId, quantity);
    }

    public boolean updateQuantity(Long cartItemId, Long userId, int quantity) {
        if (quantity <= 0) {
            return removeFromCart(cartItemId, userId);
        }

        List<CartItem> items = cartDAO.findByUserId(userId);
        CartItem item = items.stream()
                .filter(ci -> ci.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (item.getProduct() != null && item.getProduct().getStockQty() < quantity) {
            throw new ValidationException("Requested quantity exceeds available stock (" + item.getProduct().getStockQty() + ")");
        }

        return cartDAO.updateQuantity(cartItemId, userId, quantity);
    }

    public boolean removeFromCart(Long cartItemId, Long userId) {
        return cartDAO.removeItem(cartItemId, userId);
    }

    public List<CartItem> getCart(Long userId) {
        return cartDAO.findByUserId(userId);
    }

    public BigDecimal calculateTotal(List<CartItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    public int getCartCount(Long userId) {
        return cartDAO.countItems(userId);
    }

    public boolean clearCart(Long userId) {
        return cartDAO.clearCart(userId);
    }
}
