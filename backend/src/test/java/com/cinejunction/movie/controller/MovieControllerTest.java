package com.cinejunction.movie.controller;

import com.cinejunction.exception.GlobalExceptionHandler;
import com.cinejunction.exception.MovieNotFoundException;
import com.cinejunction.movie.dto.MovieRequest;
import com.cinejunction.movie.dto.MovieResponse;
import com.cinejunction.movie.dto.MovieSummaryResponse;
import com.cinejunction.movie.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class MovieControllerTest {

    private MockMvc mockMvc;
    private MovieService movieService;
    private ObjectMapper objectMapper;
    private MovieController movieController;

    @BeforeEach
    void setUp() {
        movieService = Mockito.mock(MovieService.class);
        movieController = new MovieController(movieService);
        objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(movieController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void createMovie_ReturnsCreated() throws Exception {
        MovieRequest request = new MovieRequest();
        request.setTitle("Inception");
        request.setOverview("A thief who steals corporate secrets through dream-sharing technology");
        request.setLanguage("English");
        request.setRuntime(148);
        request.setStatus(com.cinejunction.movie.enums.MovieStatus.RELEASED);
        request.setAdult(false);
        request.setBudget(160000000L);
        request.setRevenue(829895144L);
        request.setGenreIds(Set.of(1L));

        MovieResponse response = new MovieResponse();
        response.setId(1L);
        response.setTitle("Inception");

        Mockito.when(movieService.createMovie(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    void createMovie_InvalidRequest_ReturnsBadRequest() throws Exception {
        MovieRequest request = new MovieRequest();
        request.setTitle("");
        request.setLanguage("");
        request.setRuntime(-1);

        mockMvc.perform(post("/api/v1/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMovieById_ReturnsMovie() throws Exception {
        MovieResponse response = new MovieResponse();
        response.setId(1L);
        response.setTitle("Inception");

        Mockito.when(movieService.getMovieById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    void getMovieById_NotFound_Returns404() throws Exception {
        Mockito.when(movieService.getMovieById(1L)).thenThrow(new MovieNotFoundException("Movie not found"));

        mockMvc.perform(get("/api/v1/movies/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllMovies_ReturnsPage() throws Exception {
        MovieSummaryResponse summary = new MovieSummaryResponse();
        summary.setId(1L);
        summary.setTitle("Inception");

        Page<MovieSummaryResponse> page = new PageImpl<>(List.of(summary), org.springframework.data.domain.PageRequest.of(0, 1), 1);
        Mockito.when(movieService.getFilteredMovies(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Inception"));
    }

    @Test
    void searchMovies_ReturnsFilteredPage() throws Exception {
        MovieSummaryResponse summary = new MovieSummaryResponse();
        summary.setId(1L);
        summary.setTitle("Inception");

        Page<MovieSummaryResponse> page = new PageImpl<>(List.of(summary), org.springframework.data.domain.PageRequest.of(0, 1), 1);
        Mockito.when(movieService.searchMovies(eq("Inception"), any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/movies/search")
                        .param("keyword", "Inception"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Inception"));
    }

    @Test
    void updateMovie_ReturnsUpdatedMovie() throws Exception {
        MovieRequest request = new MovieRequest();
        request.setTitle("Inception Updated");
        request.setLanguage("English");
        request.setRuntime(150);

        MovieResponse response = new MovieResponse();
        response.setId(1L);
        response.setTitle("Inception Updated");

        Mockito.when(movieService.updateMovie(any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Inception Updated"));
    }

    @Test
    void deleteMovie_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/movies/1"))
                .andExpect(status().isNoContent());
    }
}
