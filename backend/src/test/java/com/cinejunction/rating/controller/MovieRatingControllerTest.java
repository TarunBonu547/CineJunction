package com.cinejunction.rating.controller;

import com.cinejunction.exception.GlobalExceptionHandler;
import com.cinejunction.rating.dto.CreateRatingRequest;
import com.cinejunction.rating.dto.MovieRatingStatsResponse;
import com.cinejunction.rating.dto.RatingResponse;
import com.cinejunction.rating.dto.UpdateRatingRequest;
import com.cinejunction.rating.service.MovieRatingService;
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

class MovieRatingControllerTest {

    private MockMvc mockMvc;
    private MovieRatingService movieRatingService;
    private ObjectMapper objectMapper;
    private MovieRatingController movieRatingController;

    @BeforeEach
    void setUp() {
        movieRatingService = Mockito.mock(MovieRatingService.class);
        movieRatingController = new MovieRatingController(movieRatingService);
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(movieRatingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void createRating_ReturnsCreated() throws Exception {
        CreateRatingRequest request = new CreateRatingRequest();
        request.setMovieId(1L);
        request.setRating(8);

        RatingResponse response = new RatingResponse();
        response.setId(1L);
        response.setMovieId(1L);
        response.setMovieTitle("Interstellar");
        response.setUserId(1L);
        response.setUsername("testuser");
        response.setRating(8);

        Mockito.when(movieRatingService.createRating(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.movieTitle").value("Interstellar"));
    }

    @Test
    void createRating_InvalidRequest_ReturnsBadRequest() throws Exception {
        CreateRatingRequest request = new CreateRatingRequest();
        request.setMovieId(null);
        request.setRating(null);

        mockMvc.perform(post("/api/v1/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRating_ReturnsUpdated() throws Exception {
        UpdateRatingRequest request = new UpdateRatingRequest();
        request.setRating(9);

        RatingResponse response = new RatingResponse();
        response.setId(1L);
        response.setRating(9);

        Mockito.when(movieRatingService.updateRating(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/ratings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(9));
    }

    @Test
    void deleteRating_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/ratings/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getMovieRatings_ReturnsList() throws Exception {
        RatingResponse rating = new RatingResponse();
        rating.setId(1L);
        rating.setMovieId(1L);
        rating.setRating(8);

        Mockito.when(movieRatingService.getMovieRatings(1L)).thenReturn(List.of(rating));

        mockMvc.perform(get("/api/v1/ratings/movie/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rating").value(8));
    }

    @Test
    void getUserRatings_ReturnsPage() throws Exception {
        RatingResponse rating = new RatingResponse();
        rating.setId(1L);
        rating.setUserId(1L);
        rating.setRating(8);

        Page<RatingResponse> page = new PageImpl<>(List.of(rating), PageRequest.of(0, 1), 1);
        Mockito.when(movieRatingService.getUserRatings(eq(1L), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/ratings/user/1")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void getMovieRatingStats_ReturnsStats() throws Exception {
        MovieRatingStatsResponse stats = new MovieRatingStatsResponse();
        stats.setMovieId(1L);
        stats.setMovieTitle("Interstellar");
        stats.setAverageRating(8.5);
        stats.setTotalRatings(2L);
        stats.setRatingDistribution(Map.of(1, 0L, 5, 1L, 10, 1L));

        Mockito.when(movieRatingService.getMovieRatingStats(1L)).thenReturn(stats);

        mockMvc.perform(get("/api/v1/ratings/movie/1/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(8.5))
                .andExpect(jsonPath("$.totalRatings").value(2));
    }
}
