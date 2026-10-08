package com.sahana.sahanamart.util;

import com.sahana.sahanamart.exception.ValidationException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Input validation utility ensuring fail-fast validation before DAO execution.
 */
public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$"
    );

    public static void validateRegistration(String name, String email, String password, String role) {
        Map<String, String> errors = new HashMap<>();

        if (name == null || name.trim().length() < 2 || name.trim().length() > 100) {
            errors.put("name", "Name must be between 2 and 100 characters");
        }

        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            errors.put("email", "A valid email address is required");
        }

        if (password == null || password.length() < 6) {
            errors.put("password", "Password must be at least 6 characters long");
        }

        if (role == null || (!role.equalsIgnoreCase("BUYER") && !role.equalsIgnoreCase("SELLER"))) {
            errors.put("role", "Role must be either BUYER or SELLER");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Registration validation failed", errors);
        }
    }

    public static void validateLogin(String email, String password) {
        Map<String, String> errors = new HashMap<>();

        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "Email cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "Password cannot be empty");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Login validation failed", errors);
        }
    }

    public static void validateProduct(String name, String description, BigDecimal price, int stockQty, String category) {
        Map<String, String> errors = new HashMap<>();

        if (name == null || name.trim().length() < 2 || name.trim().length() > 150) {
            errors.put("name", "Product name must be between 2 and 150 characters");
        }

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("price", "Price must be greater than zero");
        }

        if (stockQty < 0) {
            errors.put("stockQty", "Stock quantity cannot be negative");
        }

        if (category == null || category.trim().isEmpty()) {
            errors.put("category", "Category cannot be empty");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Product validation failed", errors);
        }
    }

    public static void validateOrder(String shippingAddress, String paymentMethod) {
        Map<String, String> errors = new HashMap<>();

        if (shippingAddress == null || shippingAddress.trim().length() < 10) {
            errors.put("shippingAddress", "Please provide a complete shipping address (minimum 10 characters)");
        }

        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            errors.put("paymentMethod", "Please select a valid payment method");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Order checkout validation failed", errors);
        }
    }

    public static void validateReview(int rating, String comment) {
        Map<String, String> errors = new HashMap<>();

        if (rating < 1 || rating > 5) {
            errors.put("rating", "Rating must be between 1 and 5 stars");
        }

        if (comment == null || comment.trim().length() < 3) {
            errors.put("comment", "Review comment must be at least 3 characters");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Review validation failed", errors);
        }
    }
}
