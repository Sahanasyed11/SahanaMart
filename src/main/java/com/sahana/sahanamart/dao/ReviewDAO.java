package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.model.Review;

import java.util.List;

public interface ReviewDAO {
    Review create(Review review);
    List<Review> findByProductId(Long productId);
    boolean hasPurchasedProduct(Long userId, Long productId);
    boolean hasReviewedProduct(Long userId, Long productId);
    double getAverageRating(Long productId);
    int getReviewCount(Long productId);
}
