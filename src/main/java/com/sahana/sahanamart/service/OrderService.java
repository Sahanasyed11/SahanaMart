package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dao.CartDAO;
import com.sahana.sahanamart.dao.CartDAOImpl;
import com.sahana.sahanamart.dao.OrderDAO;
import com.sahana.sahanamart.dao.OrderDAOImpl;
import com.sahana.sahanamart.dao.ProductDAO;
import com.sahana.sahanamart.dao.ProductDAOImpl;
import com.sahana.sahanamart.dto.OrderRequestDTO;
import com.sahana.sahanamart.exception.AppException;
import com.sahana.sahanamart.exception.ResourceNotFoundException;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.CartItem;
import com.sahana.sahanamart.model.Order;
import com.sahana.sahanamart.model.OrderItem;
import com.sahana.sahanamart.model.Product;
import com.sahana.sahanamart.util.DBUtil;
import com.sahana.sahanamart.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderDAO orderDAO;
    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public OrderService() {
        this.orderDAO = new OrderDAOImpl();
        this.cartDAO = new CartDAOImpl();
        this.productDAO = new ProductDAOImpl();
    }

    public OrderService(OrderDAO orderDAO, CartDAO cartDAO, ProductDAO productDAO) {
        this.orderDAO = orderDAO;
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    /**
     * Executes atomic checkout with ACID transaction semantics:
     * 1. Validate cart is not empty.
     * 2. Verify stock availability for all items.
     * 3. Insert into orders.
     * 4. Insert into order_items with frozen price.
     * 5. Atomically decrement product stock.
     * 6. Clear buyer's cart.
     * 7. Commit or rollback.
     */
    public Order checkout(Long buyerId, OrderRequestDTO dto) {
        ValidationUtil.validateOrder(dto.getShippingAddress(), dto.getPaymentMethod());

        List<CartItem> cartItems = cartDAO.findByUserId(buyerId);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new ValidationException("Cannot checkout with an empty cart");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cartItems) {
            Product p = productDAO.findById(ci.getProductId())
                    .orElseThrow(() -> new ValidationException("Product " + ci.getProductId() + " is no longer available"));
            if (p.getStockQty() < ci.getQuantity()) {
                throw new ValidationException("Item '" + p.getName() + "' has insufficient stock (Requested: " 
                        + ci.getQuantity() + ", Available: " + p.getStockQty() + ")");
            }
            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
        }

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // Begin ACID transaction

            Order order = new Order();
            order.setBuyerId(buyerId);
            order.setTotalAmount(total);
            order.setStatus("CONFIRMED"); // Auto-confirmed upon mock payment
            order.setShippingAddress(dto.getShippingAddress().trim());
            order.setPaymentMethod(dto.getPaymentMethod());
            order.setPaymentStatus("COMPLETED");

            Order createdOrder = orderDAO.create(order, conn);

            for (CartItem ci : cartItems) {
                Product p = ci.getProduct();
                OrderItem item = new OrderItem();
                item.setOrderId(createdOrder.getId());
                item.setProductId(ci.getProductId());
                item.setQuantity(ci.getQuantity());
                item.setUnitPrice(p.getPrice());

                orderDAO.addOrderItem(item, conn);

                // Atomic stock deduction
                boolean deducted = productDAO.deductStock(ci.getProductId(), ci.getQuantity(), conn);
                if (!deducted) {
                    throw new SQLException("Failed to deduct stock for product: " + p.getName());
                }
            }

            // Clear the buyer's cart
            cartDAO.clearCart(buyerId, conn);

            conn.commit(); // Transaction committed
            logger.info("Order successfully created and committed. Order ID: {}", createdOrder.getId());

            createdOrder.setItems(orderDAO.findItemsByOrderId(createdOrder.getId()));
            return createdOrder;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    logger.warn("Rolling back checkout transaction due to error: {}", e.getMessage());
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Failed to rollback transaction: {}", ex.getMessage());
                }
            }
            throw new AppException("Order placement failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public Order getOrderById(Long orderId, Long userId, String userRole) {
        Order order = orderDAO.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!"ADMIN".equalsIgnoreCase(userRole) && !order.getBuyerId().equals(userId)) {
            // Check if seller owns any items in this order
            boolean isSellerOfOrder = order.getItems().stream()
                    .anyMatch(item -> userId.equals(item.getSellerId()));
            if (!isSellerOfOrder) {
                throw new ValidationException("Access denied to view this order.");
            }
        }
        return order;
    }

    public List<Order> getBuyerOrders(Long buyerId) {
        return orderDAO.findByBuyerId(buyerId);
    }

    public List<OrderItem> getSellerOrders(Long sellerId) {
        return orderDAO.findItemsForSeller(sellerId);
    }

    public List<Order> getAllOrders(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return orderDAO.findAll(pageSize, offset);
    }

    public boolean updateOrderStatus(Long orderId, String newStatus) {
        // Workflow: PENDING -> CONFIRMED -> SHIPPED -> DELIVERED (or CANCELLED)
        String s = newStatus.toUpperCase();
        if (!s.equals("PENDING") && !s.equals("CONFIRMED") && !s.equals("SHIPPED") && !s.equals("DELIVERED") && !s.equals("CANCELLED")) {
            throw new ValidationException("Invalid status: " + newStatus);
        }
        return orderDAO.updateStatus(orderId, s);
    }

    public long getTotalOrderCount() {
        return orderDAO.count();
    }

    public BigDecimal getTotalSalesRevenue() {
        return orderDAO.calculateTotalRevenue();
    }
}
