package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.model.WishlistItem;

import java.util.List;

public interface WishlistDAO {
    boolean addToWishlist(Long userId, Long productId);
    boolean removeFromWishlist(Long userId, Long productId);
    boolean isInWishlist(Long userId, Long productId);
    List<WishlistItem> findByUserId(Long userId);
}
