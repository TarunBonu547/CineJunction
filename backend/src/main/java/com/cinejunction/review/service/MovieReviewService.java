package com.cinejunction.review.service;

import com.cinejunction.review.dto.CreateReviewRequest;
import com.cinejunction.review.dto.MovieReviewStatsResponse;
import com.cinejunction.review.dto.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovieReviewService {

    ReviewResponse createReview(CreateReviewRequest request);

    ReviewResponse updateReview(Long reviewId, com.cinejunction.review.dto.UpdateReviewRequest request);

    void deleteReview(Long reviewId);

    ReviewResponse getReview(Long reviewId);

    Page<ReviewResponse> getMovieReviews(Long movieId, Pageable pageable);

    Page<ReviewResponse> getUserReviews(Long userId, Pageable pageable);

    MovieReviewStatsResponse getMovieReviewStats(Long movieId);

    Page<ReviewResponse> searchReviews(String keyword, Pageable pageable);
}
