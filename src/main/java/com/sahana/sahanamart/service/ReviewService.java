package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dao.ReviewDAO;
import com.sahana.sahanamart.dao.ReviewDAOImpl;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.Review;
import com.sahana.sahanamart.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ReviewService {
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    private final ReviewDAO reviewDAO;

    public ReviewService() {
        this.reviewDAO = new ReviewDAOImpl();
    }

    public ReviewService(ReviewDAO reviewDAO) {
        this.reviewDAO = reviewDAO;
    }

    public Review addReview(Long userId, Long productId, int rating, String comment) {
        ValidationUtil.validateReview(rating, comment);

        // F8 rule: Must have completed purchase before reviewing
        if (!reviewDAO.hasPurchasedProduct(userId, productId)) {
            throw new ValidationException("You can only review products you have purchased and received.");
        }

        if (reviewDAO.hasReviewedProduct(userId, productId)) {
            throw new ValidationException("You have already reviewed this product.");
        }

        Review review = new Review();
        review.setUserId(userId);
        review.setProductId(productId);
        review.setRating(rating);
        review.setComment(comment.trim());

        return reviewDAO.create(review);
    }

    public List<Review> getProductReviews(Long productId) {
        return reviewDAO.findByProductId(productId);
    }

    public boolean canUserReview(Long userId, Long productId) {
        return reviewDAO.hasPurchasedProduct(userId, productId) && !reviewDAO.hasReviewedProduct(userId, productId);
    }

    public double getAverageRating(Long productId) {
        return reviewDAO.getAverageRating(productId);
    }

    public int getReviewCount(Long productId) {
        return reviewDAO.getReviewCount(productId);
    }
}
