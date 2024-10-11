package org.gad.ecommerce_computer_components.service.impl;

import org.gad.ecommerce_computer_components.persistence.entity.Product;
import org.gad.ecommerce_computer_components.persistence.entity.Review;
import org.gad.ecommerce_computer_components.persistence.entity.UserEntity;
import org.gad.ecommerce_computer_components.persistence.repository.ProductRepository;
import org.gad.ecommerce_computer_components.persistence.repository.ReviewRepository;
import org.gad.ecommerce_computer_components.persistence.repository.UserRepository;
import org.gad.ecommerce_computer_components.presentation.dto.request.ReviewDTO;
import org.gad.ecommerce_computer_components.service.interfaces.ReviewService;
import org.gad.ecommerce_computer_components.utils.mappers.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void createReview(Long userId, ReviewDTO reviewDTO, Long productId) {
        Review review = ReviewMapper.INSTANCE.reviewDTOToReviewEntity(reviewDTO);
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }
        review.setProduct(product);
        review.setUser(userOptional.get());
        reviewRepository.save(review);
    }

    @Override
    public void updateReview(Long reviewId, ReviewDTO reviewDTO) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));
        review.setQualification(reviewDTO.getQualification());
        review.setComment(reviewDTO.getComment());
        reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));
        reviewRepository.delete(review);
    }
}
