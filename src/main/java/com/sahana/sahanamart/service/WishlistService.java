package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dao.CartDAO;
import com.sahana.sahanamart.dao.CartDAOImpl;
import com.sahana.sahanamart.dao.WishlistDAO;
import com.sahana.sahanamart.dao.WishlistDAOImpl;
import com.sahana.sahanamart.model.WishlistItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WishlistService {
    private static final Logger logger = LoggerFactory.getLogger(WishlistService.class);
    private final WishlistDAO wishlistDAO;
    private final CartDAO cartDAO;

    public WishlistService() {
        this.wishlistDAO = new WishlistDAOImpl();
        this.cartDAO = new CartDAOImpl();
    }

    public WishlistService(WishlistDAO wishlistDAO, CartDAO cartDAO) {
        this.wishlistDAO = wishlistDAO;
        this.cartDAO = cartDAO;
    }

    public boolean toggleWishlist(Long userId, Long productId) {
        if (wishlistDAO.isInWishlist(userId, productId)) {
            return wishlistDAO.removeFromWishlist(userId, productId);
        } else {
            return wishlistDAO.addToWishlist(userId, productId);
        }
    }

    public boolean addToWishlist(Long userId, Long productId) {
        return wishlistDAO.addToWishlist(userId, productId);
    }

    public boolean removeFromWishlist(Long userId, Long productId) {
        return wishlistDAO.removeFromWishlist(userId, productId);
    }

    public boolean isInWishlist(Long userId, Long productId) {
        return wishlistDAO.isInWishlist(userId, productId);
    }

    public List<WishlistItem> getWishlist(Long userId) {
        return wishlistDAO.findByUserId(userId);
    }

    public boolean moveToCart(Long userId, Long productId) {
        cartDAO.addToCart(userId, productId, 1);
        wishlistDAO.removeFromWishlist(userId, productId);
        return true;
    }
}
