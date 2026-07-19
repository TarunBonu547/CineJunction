package com.cinejunction.review.service.impl;

import com.cinejunction.entity.User;
import com.cinejunction.exception.MovieNotFoundException;
import com.cinejunction.exception.UserNotFoundException;
import com.cinejunction.movie.entity.Movie;
import com.cinejunction.movie.repository.MovieRepository;
import com.cinejunction.review.dto.CreateReviewRequest;
import com.cinejunction.review.dto.MovieReviewStatsResponse;
import com.cinejunction.review.dto.ReviewResponse;
import com.cinejunction.review.dto.UpdateReviewRequest;
import com.cinejunction.review.entity.MovieReview;
import com.cinejunction.review.exception.ReviewAlreadyExistsException;
import com.cinejunction.review.exception.ReviewNotFoundException;
import com.cinejunction.review.exception.UnauthorizedReviewAccessException;
import com.cinejunction.review.mapper.MovieReviewMapper;
import com.cinejunction.review.repository.MovieReviewRepository;
import com.cinejunction.review.service.MovieReviewService;
import com.cinejunction.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MovieReviewServiceImpl implements MovieReviewService {

    private final MovieReviewRepository movieReviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final MovieReviewMapper movieReviewMapper;

    @Override
    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request) {
        User currentUser = getCurrentUser();

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + request.getMovieId()));

        boolean exists = movieReviewRepository.existsByMovieAndUser(movie, currentUser);
        if (exists) {
            throw new ReviewAlreadyExistsException(
                    "User has already reviewed movie with id: " + request.getMovieId()
            );
        }

        MovieReview movieReview = movieReviewMapper.toEntity(request);
        movieReview.setMovie(movie);
        movieReview.setUser(currentUser);
        movieReviewRepository.save(movieReview);

        return movieReviewMapper.toResponse(movieReview);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long reviewId, UpdateReviewRequest request) {
        User currentUser = getCurrentUser();

        MovieReview movieReview = movieReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));

        if (!movieReview.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedReviewAccessException(
                    "User is not authorized to update this review"
            );
        }

        movieReview.setTitle(request.getTitle());
        movieReview.setReviewText(request.getReviewText());
        movieReview.setContainsSpoilers(request.getContainsSpoilers() != null ? request.getContainsSpoilers() : false);
        movieReviewRepository.save(movieReview);

        return movieReviewMapper.toResponse(movieReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        User currentUser = getCurrentUser();

        MovieReview movieReview = movieReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));

        if (!movieReview.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedReviewAccessException(
                    "User is not authorized to delete this review"
            );
        }

        movieReviewRepository.delete(movieReview);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getReview(Long reviewId) {
        MovieReview movieReview = movieReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));

        return movieReviewMapper.toResponse(movieReview);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getMovieReviews(Long movieId, Pageable pageable) {
        if (!movieRepository.existsById(movieId)) {
            throw new MovieNotFoundException("Movie not found with id: " + movieId);
        }

        return movieReviewRepository.findByMovieId(movieId, pageable)
                .map(movieReviewMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getUserReviews(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        return movieReviewRepository.findByUserId(userId, pageable)
                .map(movieReviewMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public MovieReviewStatsResponse getMovieReviewStats(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new MovieNotFoundException("Movie not found with id: " + movieId);
        }

        Movie movie = movieRepository.findById(movieId).orElseThrow();
        long totalReviews = movieReviewRepository.countByMovie(movie);
        long spoilerReviews = movieReviewRepository.findByMovieIdAndContainsSpoilersTrue(movieId).size();
        long nonSpoilerReviews = totalReviews - spoilerReviews;

        return MovieReviewStatsResponse.builder()
                .movieId(movieId)
                .movieTitle(movie.getTitle())
                .totalReviews(totalReviews)
                .spoilerReviews(spoilerReviews)
                .nonSpoilerReviews(nonSpoilerReviews)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> searchReviews(String keyword, Pageable pageable) {
        return movieReviewRepository.searchReviews(keyword, pageable)
                .map(movieReviewMapper::toResponse);
    }

    private User getCurrentUser() {
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email = userDetails.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }
}
