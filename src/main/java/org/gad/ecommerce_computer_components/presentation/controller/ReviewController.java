package org.gad.ecommerce_computer_components.presentation.controller;

import jakarta.validation.Valid;
import org.gad.ecommerce_computer_components.presentation.dto.request.ReviewDTO;
import org.gad.ecommerce_computer_components.presentation.dto.response.ApiResponse;
import org.gad.ecommerce_computer_components.service.interfaces.ReviewService;
import org.gad.ecommerce_computer_components.service.interfaces.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/create/review/{productId}/product")
    public ResponseEntity<ApiResponse> createReview(@RequestHeader(value = "Authorization") String authorizationHeader,
                                                    @RequestBody @Valid ReviewDTO reviewDTO,
                                                    @PathVariable Long productId) {

        Long userId = shoppingCartService.extractUserIdFromToken(authorizationHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid token"));
        }

        if (productId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Product id is required"));
        }

        try {
            reviewService.createReview(userId, reviewDTO, productId);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Review created successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @PutMapping("/update/review/{reviewId}")
    public ResponseEntity<ApiResponse> updateReview(@PathVariable Long reviewId, @RequestBody @Valid ReviewDTO reviewDTO) {
        if (reviewId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Review id is required"));
        }

        try {
            reviewService.updateReview(reviewId, reviewDTO);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Review updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/delete/review/{reviewId}")
    public ResponseEntity<ApiResponse> deleteReview(@PathVariable Long reviewId) {
        if (reviewId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Review id is required"));
        }

        try {
            reviewService.deleteReview(reviewId);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Review deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }
}
