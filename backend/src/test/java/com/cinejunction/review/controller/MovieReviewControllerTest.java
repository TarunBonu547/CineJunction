package com.cinejunction.review.controller;

import com.cinejunction.exception.GlobalExceptionHandler;
import com.cinejunction.review.dto.CreateReviewRequest;
import com.cinejunction.review.dto.MovieReviewStatsResponse;
import com.cinejunction.review.dto.ReviewResponse;
import com.cinejunction.review.dto.UpdateReviewRequest;
import com.cinejunction.review.service.MovieReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class MovieReviewControllerTest {

    private MockMvc mockMvc;
    private MovieReviewService movieReviewService;
    private ObjectMapper objectMapper;
    private MovieReviewController movieReviewController;

    @BeforeEach
    void setUp() {
        movieReviewService = Mockito.mock(MovieReviewService.class);
        movieReviewController = new MovieReviewController(movieReviewService);
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(movieReviewController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void createReview_ReturnsCreated() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest();
        request.setMovieId(1L);
        request.setTitle("Great movie");
        request.setReviewText("This is a great movie with amazing plot twists and stunning visuals.");
        request.setContainsSpoilers(false);

        ReviewResponse response = new ReviewResponse();
        response.setId(1L);
        response.setMovieId(1L);
        response.setMovieTitle("Inception");
        response.setUserId(1L);
        response.setUsername("testuser");
        response.setTitle("Great movie");
        response.setReviewText("This is a great movie with amazing plot twists and stunning visuals.");
        response.setContainsSpoilers(false);

        Mockito.when(movieReviewService.createReview(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.movieTitle").value("Inception"));
    }

    @Test
    void createReview_InvalidRequest_ReturnsBadRequest() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest();
        request.setMovieId(null);
        request.setTitle("AB");
        request.setReviewText("Short");

        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateReview_ReturnsUpdated() throws Exception {
        UpdateReviewRequest request = new UpdateReviewRequest();
        request.setTitle("Updated title");
        request.setReviewText("Updated review text with more than twenty characters.");
        request.setContainsSpoilers(true);

        ReviewResponse response = new ReviewResponse();
        response.setId(1L);
        response.setTitle("Updated title");

        Mockito.when(movieReviewService.updateReview(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated title"));
    }

    @Test
    void deleteReview_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/reviews/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getReview_ReturnsReview() throws Exception {
        ReviewResponse response = new ReviewResponse();
        response.setId(1L);
        response.setTitle("Great movie");

        Mockito.when(movieReviewService.getReview(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Great movie"));
    }

    @Test
    void getMovieReviews_ReturnsList() throws Exception {
        ReviewResponse review = new ReviewResponse();
        review.setId(1L);
        review.setMovieId(1L);
        review.setTitle("Great movie");

        Page<ReviewResponse> page = new PageImpl<>(List.of(review), PageRequest.of(0, 1), 1);
        Mockito.when(movieReviewService.getMovieReviews(eq(1L), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/reviews/movie/1")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void getUserReviews_ReturnsList() throws Exception {
        ReviewResponse review = new ReviewResponse();
        review.setId(1L);
        review.setUserId(1L);
        review.setTitle("Great movie");

        Page<ReviewResponse> page = new PageImpl<>(List.of(review), PageRequest.of(0, 1), 1);
        Mockito.when(movieReviewService.getUserReviews(eq(1L), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/reviews/user/1")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void getMovieReviewStats_ReturnsStats() throws Exception {
        MovieReviewStatsResponse stats = new MovieReviewStatsResponse();
        stats.setMovieId(1L);
        stats.setMovieTitle("Inception");
        stats.setTotalReviews(5L);
        stats.setSpoilerReviews(2L);
        stats.setNonSpoilerReviews(3L);

        Mockito.when(movieReviewService.getMovieReviewStats(1L)).thenReturn(stats);

        mockMvc.perform(get("/api/v1/reviews/movie/1/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReviews").value(5))
                .andExpect(jsonPath("$.spoilerReviews").value(2))
                .andExpect(jsonPath("$.nonSpoilerReviews").value(3));
    }

    @Test
    void searchReviews_ReturnsResults() throws Exception {
        ReviewResponse review = new ReviewResponse();
        review.setId(1L);
        review.setTitle("Great movie");

        Page<ReviewResponse> page = new PageImpl<>(List.of(review), PageRequest.of(0, 1), 1);
        Mockito.when(movieReviewService.searchReviews(eq("Great"), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/reviews/search")
                        .param("keyword", "Great")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Great movie"));
    }
}
