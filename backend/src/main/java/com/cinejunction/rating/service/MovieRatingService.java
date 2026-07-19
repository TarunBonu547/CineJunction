package com.cinejunction.rating.service;

import com.cinejunction.rating.dto.CreateRatingRequest;
import com.cinejunction.rating.dto.MovieRatingStatsResponse;
import com.cinejunction.rating.dto.RatingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MovieRatingService {

    RatingResponse createRating(CreateRatingRequest request);

    RatingResponse updateRating(Long ratingId, Integer rating);

    void deleteRating(Long ratingId);

    List<RatingResponse> getMovieRatings(Long movieId);

    Page<RatingResponse> getUserRatings(Long userId, Pageable pageable);

    RatingResponse getMovieRating(Long movieId);

    MovieRatingStatsResponse getMovieRatingStats(Long movieId);
}
