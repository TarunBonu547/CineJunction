package com.cinejunction.rating.service.impl;

import com.cinejunction.entity.User;
import com.cinejunction.exception.MovieNotFoundException;
import com.cinejunction.exception.UserNotFoundException;
import com.cinejunction.movie.entity.Movie;
import com.cinejunction.movie.repository.MovieRepository;
import com.cinejunction.rating.dto.CreateRatingRequest;
import com.cinejunction.rating.dto.MovieRatingStatsResponse;
import com.cinejunction.rating.dto.RatingResponse;
import com.cinejunction.rating.entity.MovieRating;
import com.cinejunction.rating.exception.RatingAlreadyExistsException;
import com.cinejunction.rating.exception.RatingNotFoundException;
import com.cinejunction.rating.exception.UnauthorizedRatingAccessException;
import com.cinejunction.rating.mapper.MovieRatingMapper;
import com.cinejunction.rating.repository.MovieRatingRepository;
import com.cinejunction.rating.service.MovieRatingService;
import com.cinejunction.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieRatingServiceImpl implements MovieRatingService {

    private final MovieRatingRepository movieRatingRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final MovieRatingMapper movieRatingMapper;

    @Override
    @Transactional
    public RatingResponse createRating(CreateRatingRequest request) {
        User currentUser = getCurrentUser();

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + request.getMovieId()));

        boolean exists = movieRatingRepository.existsByMovieAndUser(movie, currentUser);
        if (exists) {
            throw new RatingAlreadyExistsException(
                    "User has already rated movie with id: " + request.getMovieId()
            );
        }

        MovieRating movieRating = movieRatingMapper.toEntity(request);
        movieRating.setMovie(movie);
        movieRating.setUser(currentUser);
        movieRatingRepository.save(movieRating);

        return movieRatingMapper.toResponse(movieRating);
    }

    @Override
    @Transactional
    public RatingResponse updateRating(Long ratingId, Integer rating) {
        User currentUser = getCurrentUser();

        MovieRating movieRating = movieRatingRepository.findById(ratingId)
                .orElseThrow(() -> new RatingNotFoundException("Rating not found with id: " + ratingId));

        if (!movieRating.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedRatingAccessException(
                    "User is not authorized to update this rating"
            );
        }

        movieRating.setRating(rating);
        movieRatingRepository.save(movieRating);

        return movieRatingMapper.toResponse(movieRating);
    }

    @Override
    @Transactional
    public void deleteRating(Long ratingId) {
        User currentUser = getCurrentUser();

        MovieRating movieRating = movieRatingRepository.findById(ratingId)
                .orElseThrow(() -> new RatingNotFoundException("Rating not found with id: " + ratingId));

        if (!movieRating.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedRatingAccessException(
                    "User is not authorized to delete this rating"
            );
        }

        movieRatingRepository.delete(movieRating);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RatingResponse> getMovieRatings(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new MovieNotFoundException("Movie not found with id: " + movieId);
        }

        return movieRatingRepository.findByMovieId(movieId).stream()
                .map(movieRatingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RatingResponse> getUserRatings(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        return movieRatingRepository.findByUserId(userId, pageable)
                .map(movieRatingMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public RatingResponse getMovieRating(Long movieId) {
        User currentUser = getCurrentUser();

        MovieRating movieRating = movieRatingRepository.findByMovieIdAndUserId(movieId, currentUser.getId())
                .orElseThrow(() -> new RatingNotFoundException(
                        "Rating not found for movie id: " + movieId + " by user id: " + currentUser.getId()
                ));

        return movieRatingMapper.toResponse(movieRating);
    }

    @Override
    @Transactional(readOnly = true)
    public MovieRatingStatsResponse getMovieRatingStats(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new MovieNotFoundException("Movie not found with id: " + movieId);
        }

        Movie movie = movieRepository.findById(movieId).orElseThrow();
        Double averageRating = movieRatingRepository.findAverageRatingByMovieId(movieId);
        long totalRatings = movieRatingRepository.countByMovie(movie);

        List<Object[]> distributionResults = movieRatingRepository.findRatingDistributionByMovieId(movieId);
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            ratingDistribution.put(i, 0L);
        }
        for (Object[] result : distributionResults) {
            Integer ratingValue = ((Number) result[0]).intValue();
            Long count = ((Number) result[1]).longValue();
            ratingDistribution.put(ratingValue, count);
        }

        return MovieRatingStatsResponse.builder()
                .movieId(movieId)
                .movieTitle(movie.getTitle())
                .averageRating(averageRating != null ? averageRating : 0.0)
                .totalRatings(totalRatings)
                .ratingDistribution(ratingDistribution)
                .build();
    }

    private User getCurrentUser() {
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email = userDetails.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }
}
