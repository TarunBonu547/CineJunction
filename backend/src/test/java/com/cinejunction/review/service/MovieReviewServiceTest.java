package com.cinejunction.review.service;

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
import com.cinejunction.review.service.impl.MovieReviewServiceImpl;
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

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieReviewServiceTest {

    @Mock
    private MovieReviewRepository movieReviewRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MovieReviewMapper movieReviewMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private MovieReviewServiceImpl movieReviewService;

    private Movie movie;
    private User user;
    private CustomUserDetails userDetails;
    private MovieReview movieReview;
    private CreateReviewRequest createRequest;
    private UpdateReviewRequest updateRequest;
    private ReviewResponse reviewResponse;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(com.cinejunction.enums.Role.USER);
        user.setEnabled(true);

        userDetails = new CustomUserDetails(user);

        movieReview = new MovieReview();
        movieReview.setId(1L);
        movieReview.setMovie(movie);
        movieReview.setUser(user);
        movieReview.setTitle("Great movie");
        movieReview.setReviewText("This is a great movie with amazing plot twists and stunning visuals.");
        movieReview.setContainsSpoilers(false);

        createRequest = new CreateReviewRequest();
        createRequest.setMovieId(1L);
        createRequest.setTitle("Great movie");
        createRequest.setReviewText("This is a great movie with amazing plot twists and stunning visuals.");
        createRequest.setContainsSpoilers(false);

        updateRequest = new UpdateReviewRequest();
        updateRequest.setTitle("Updated title");
        updateRequest.setReviewText("Updated review text with more than twenty characters.");
        updateRequest.setContainsSpoilers(true);

        reviewResponse = new ReviewResponse();
        reviewResponse.setId(1L);
        reviewResponse.setMovieId(1L);
        reviewResponse.setMovieTitle("Inception");
        reviewResponse.setUserId(1L);
        reviewResponse.setUsername("testuser");
        reviewResponse.setTitle("Great movie");
        reviewResponse.setReviewText("This is a great movie with amazing plot twists and stunning visuals.");
        reviewResponse.setContainsSpoilers(false);
        reviewResponse.setCreatedAt(Instant.now());
        reviewResponse.setUpdatedAt(Instant.now());

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createReview_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(movieReviewRepository.existsByMovieAndUser(movie, user)).thenReturn(false);
        when(movieReviewMapper.toEntity(any())).thenReturn(movieReview);
        when(movieReviewRepository.save(any())).thenReturn(movieReview);
        when(movieReviewMapper.toResponse(any())).thenReturn(reviewResponse);

        ReviewResponse response = movieReviewService.createReview(createRequest);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Great movie");
        verify(movieReviewRepository).save(any());
    }

    @Test
    void createReview_MovieNotFound_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> movieReviewService.createReview(createRequest));
    }

    @Test
    void createReview_Duplicate_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(movieReviewRepository.existsByMovieAndUser(movie, user)).thenReturn(true);

        assertThrows(ReviewAlreadyExistsException.class, () -> movieReviewService.createReview(createRequest));
    }

    @Test
    void updateReview_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(movieReviewRepository.findById(1L)).thenReturn(Optional.of(movieReview));
        when(movieReviewRepository.save(any())).thenReturn(movieReview);
        when(movieReviewMapper.toResponse(any())).thenReturn(reviewResponse);

        ReviewResponse response = movieReviewService.updateReview(1L, updateRequest);

        assertThat(response).isNotNull();
    }

    @Test
    void updateReview_NotFound_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(movieReviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () -> movieReviewService.updateReview(1L, updateRequest));
    }

    @Test
    void updateReview_Unauthorized_ThrowsException() {
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
        when(movieReviewRepository.findById(1L)).thenReturn(Optional.of(movieReview));

        assertThrows(UnauthorizedReviewAccessException.class, () -> movieReviewService.updateReview(1L, updateRequest));
    }

    @Test
    void deleteReview_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(movieReviewRepository.findById(1L)).thenReturn(Optional.of(movieReview));

        movieReviewService.deleteReview(1L);

        verify(movieReviewRepository).delete(movieReview);
    }

    @Test
    void deleteReview_NotFound_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(movieReviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () -> movieReviewService.deleteReview(1L));
    }

    @Test
    void getReview_Success() {
        when(movieReviewRepository.findById(1L)).thenReturn(Optional.of(movieReview));
        when(movieReviewMapper.toResponse(any())).thenReturn(reviewResponse);

        ReviewResponse response = movieReviewService.getReview(1L);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Great movie");
    }

    @Test
    void getMovieReviews_Success() {
        Page<MovieReview> page = new PageImpl<>(List.of(movieReview));
        when(movieRepository.existsById(1L)).thenReturn(true);
        when(movieReviewRepository.findByMovieId(eq(1L), any(Pageable.class))).thenReturn(page);
        when(movieReviewMapper.toResponse(any())).thenReturn(reviewResponse);

        Page<ReviewResponse> reviews = movieReviewService.getMovieReviews(1L, Pageable.unpaged());

        assertThat(reviews).isNotNull();
        assertThat(reviews.getContent()).hasSize(1);
    }

    @Test
    void getUserReviews_Success() {
        Page<MovieReview> page = new PageImpl<>(List.of(movieReview));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(movieReviewRepository.findByUserId(eq(1L), any(Pageable.class))).thenReturn(page);
        when(movieReviewMapper.toResponse(any())).thenReturn(reviewResponse);

        Page<ReviewResponse> reviews = movieReviewService.getUserReviews(1L, Pageable.unpaged());

        assertThat(reviews).isNotNull();
        assertThat(reviews.getContent()).hasSize(1);
    }

    @Test
    void getMovieReviewStats_Success() {
        when(movieRepository.existsById(1L)).thenReturn(true);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieReviewRepository.countByMovie(any())).thenReturn(3L);
        when(movieReviewRepository.findByMovieIdAndContainsSpoilersTrue(1L)).thenReturn(List.of(movieReview, movieReview));

        MovieReviewStatsResponse stats = movieReviewService.getMovieReviewStats(1L);

        assertThat(stats).isNotNull();
        assertThat(stats.getTotalReviews()).isEqualTo(3L);
        assertThat(stats.getSpoilerReviews()).isEqualTo(2L);
        assertThat(stats.getNonSpoilerReviews()).isEqualTo(1L);
    }

    @Test
    void searchReviews_Success() {
        Page<MovieReview> page = new PageImpl<>(List.of(movieReview));
        when(movieReviewRepository.searchReviews(eq("Inception"), any(Pageable.class))).thenReturn(page);
        when(movieReviewMapper.toResponse(any())).thenReturn(reviewResponse);

        Page<ReviewResponse> reviews = movieReviewService.searchReviews("Inception", Pageable.unpaged());

        assertThat(reviews).isNotNull();
        assertThat(reviews.getContent()).hasSize(1);
    }
}
