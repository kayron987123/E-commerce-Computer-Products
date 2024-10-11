package org.gad.ecommerce_computer_components.service.interfaces;

import org.gad.ecommerce_computer_components.presentation.dto.request.ReviewDTO;

import java.math.BigDecimal;

public interface ReviewService {
    void createReview(Long userId, ReviewDTO reviewDTO, Long productId);
    void updateReview(Long reviewId, ReviewDTO reviewDTO);
    void deleteReview(Long reviewId);
}
