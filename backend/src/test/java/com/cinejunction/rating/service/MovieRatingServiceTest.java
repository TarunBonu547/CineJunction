package com.cinejunction.rating.service;

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
import com.cinejunction.rating.service.impl.MovieRatingServiceImpl;
import com.cinejunction.repository.UserRepository;
import com.cinejunction.security.service.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieRatingServiceTest {

    @Mock
    private MovieRatingRepository movieRatingRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MovieRatingMapper movieRatingMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private MovieRatingServiceImpl movieRatingService;

    private Movie movie;
    private User user;
    private CustomUserDetails userDetails;
    private MovieRating movieRating;
    private CreateRatingRequest createRequest;
    private RatingResponse ratingResponse;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Interstellar");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(com.cinejunction.enums.Role.USER);
        user.setEnabled(true);

        userDetails = new CustomUserDetails(user);

        movieRating = new MovieRating();
        movieRating.setId(1L);
        movieRating.setMovie(movie);
        movieRating.setUser(user);
        movieRating.setRating(8);

        createRequest = new CreateRatingRequest();
        createRequest.setMovieId(1L);
        createRequest.setRating(8);

        ratingResponse = new RatingResponse();
        ratingResponse.setId(1L);
        ratingResponse.setMovieId(1L);
        ratingResponse.setMovieTitle("Interstellar");
        ratingResponse.setUserId(1L);
        ratingResponse.setUsername("testuser");
        ratingResponse.setRating(8);
        ratingResponse.setCreatedAt(Instant.now());
        ratingResponse.setUpdatedAt(Instant.now());

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createRating_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(movieRatingRepository.existsByMovieAndUser(movie, user)).thenReturn(false);
        when(movieRatingMapper.toEntity(any())).thenReturn(movieRating);
        when(movieRatingRepository.save(any())).thenReturn(movieRating);
        when(movieRatingMapper.toResponse(any())).thenReturn(ratingResponse);

        RatingResponse response = movieRatingService.createRating(createRequest);

        assertThat(response).isNotNull();
        assertThat(response.getMovieTitle()).isEqualTo("Interstellar");
        verify(movieRatingRepository).save(any());
    }

    @Test
    void createRating_MovieNotFound_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> movieRatingService.createRating(createRequest));
    }

    @Test
    void createRating_Duplicate_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(movieRatingRepository.existsByMovieAndUser(movie, user)).thenReturn(true);

        assertThrows(RatingAlreadyExistsException.class, () -> movieRatingService.createRating(createRequest));
    }

    @Test
    void updateRating_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(movieRatingRepository.findById(1L)).thenReturn(Optional.of(movieRating));
        when(movieRatingRepository.save(any(MovieRating.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(movieRatingMapper.toResponse(any())).thenAnswer(invocation -> {
            MovieRating mr = invocation.getArgument(0);
            ratingResponse.setRating(mr.getRating());
            return ratingResponse;
        });

        RatingResponse response = movieRatingService.updateRating(1L, 9);

        assertThat(response).isNotNull();
        assertThat(response.getRating()).isEqualTo(9);
    }

    @Test
    void updateRating_NotFound_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(movieRatingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RatingNotFoundException.class, () -> movieRatingService.updateRating(1L, 9));
    }

    @Test
    void updateRating_Unauthorized_ThrowsException() {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@example.com");
        otherUser.setPassword("password");
        otherUser.setRole(com.cinejunction.enums.Role.USER);
        otherUser.setEnabled(true);
        CustomUserDetails otherUserDetails = new CustomUserDetails(otherUser);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(otherUserDetails);
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));
        when(movieRatingRepository.findById(1L)).thenReturn(Optional.of(movieRating));

        assertThrows(UnauthorizedRatingAccessException.class, () -> movieRatingService.updateRating(1L, 9));
    }

    @Test
    void deleteRating_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(movieRatingRepository.findById(1L)).thenReturn(Optional.of(movieRating));

        movieRatingService.deleteRating(1L);

        verify(movieRatingRepository).delete(movieRating);
    }

    @Test
    void deleteRating_NotFound_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(movieRatingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RatingNotFoundException.class, () -> movieRatingService.deleteRating(1L));
    }

    @Test
    void getMovieRatings_Success() {
        when(movieRepository.existsById(1L)).thenReturn(true);
        when(movieRatingRepository.findByMovieId(1L)).thenReturn(List.of(movieRating));
        when(movieRatingMapper.toResponse(any())).thenReturn(ratingResponse);

        List<RatingResponse> ratings = movieRatingService.getMovieRatings(1L);

        assertThat(ratings).hasSize(1);
    }

    @Test
    void getMovieRatings_MovieNotFound_ThrowsException() {
        when(movieRepository.existsById(1L)).thenReturn(false);

        assertThrows(MovieNotFoundException.class, () -> movieRatingService.getMovieRatings(1L));
    }

    @Test
    void getUserRatings_Success() {
        Page<MovieRating> page = new PageImpl<>(List.of(movieRating));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(movieRatingRepository.findByUserId(eq(1L), any(Pageable.class))).thenReturn(page);
        when(movieRatingMapper.toResponse(any())).thenReturn(ratingResponse);

        Page<RatingResponse> ratings = movieRatingService.getUserRatings(1L, Pageable.unpaged());

        assertThat(ratings).isNotNull();
        assertThat(ratings.getContent()).hasSize(1);
    }

    @Test
    void getUserRatings_UserNotFound_ThrowsException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> movieRatingService.getUserRatings(1L, Pageable.unpaged()));
    }

    @Test
    void getMovieRating_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(movieRatingRepository.findByMovieIdAndUserId(1L, 1L)).thenReturn(Optional.of(movieRating));
        when(movieRatingMapper.toResponse(any())).thenReturn(ratingResponse);

        RatingResponse response = movieRatingService.getMovieRating(1L);

        assertThat(response).isNotNull();
        assertThat(response.getRating()).isEqualTo(8);
    }

    @Test
    void getMovieRatingStats_Success() {
        when(movieRepository.existsById(1L)).thenReturn(true);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieRatingRepository.findAverageRatingByMovieId(1L)).thenReturn(8.5);
        when(movieRatingRepository.countByMovie(any())).thenReturn(3L);
        when(movieRatingRepository.findRatingDistributionByMovieId(1L)).thenReturn(List.of(
                new Object[]{1, 1L},
                new Object[]{5, 1L},
                new Object[]{10, 1L}
        ));

        MovieRatingStatsResponse stats = movieRatingService.getMovieRatingStats(1L);

        assertThat(stats).isNotNull();
        assertThat(stats.getAverageRating()).isEqualTo(8.5);
        assertThat(stats.getTotalRatings()).isEqualTo(3L);
    }
}
