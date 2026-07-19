package com.cinejunction.genre.controller;

import com.cinejunction.exception.GenreNotFoundException;
import com.cinejunction.exception.GlobalExceptionHandler;
import com.cinejunction.genre.dto.GenreRequest;
import com.cinejunction.genre.dto.GenreResponse;
import com.cinejunction.genre.service.GenreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;

class GenreControllerTest {

    private MockMvc mockMvc;
    private GenreService genreService;
    private ObjectMapper objectMapper;
    private GenreController genreController;

    @BeforeEach
    void setUp() {
        genreService = Mockito.mock(GenreService.class);
        genreController = new GenreController(genreService);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(genreController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void createGenre_ReturnsCreated() throws Exception {
        GenreRequest request = new GenreRequest();
        request.setName("Action");

        GenreResponse response = new GenreResponse();
        response.setId(1L);
        response.setName("Action");

        Mockito.when(genreService.createGenre(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Action"));
    }

    @Test
    void createGenre_InvalidRequest_ReturnsBadRequest() throws Exception {
        GenreRequest request = new GenreRequest();
        request.setName("");

        mockMvc.perform(post("/api/v1/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllGenres_ReturnsPage() throws Exception {
        GenreResponse genre = new GenreResponse();
        genre.setId(1L);
        genre.setName("Action");

        Page<GenreResponse> page = new PageImpl<>(List.of(genre), PageRequest.of(0, 1), 1);
        Mockito.when(genreService.getAllGenres(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Action"));
    }

    @Test
    void getGenreById_ReturnsGenre() throws Exception {
        GenreResponse response = new GenreResponse();
        response.setId(1L);
        response.setName("Action");

        Mockito.when(genreService.getGenreById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/genres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Action"));
    }

    @Test
    void getGenreById_NotFound_Returns404() throws Exception {
        Mockito.when(genreService.getGenreById(1L)).thenThrow(new GenreNotFoundException("Genre not found with id: 1"));

        mockMvc.perform(get("/api/v1/genres/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateGenre_ReturnsUpdatedGenre() throws Exception {
        GenreRequest request = new GenreRequest();
        request.setName("Action Updated");

        GenreResponse response = new GenreResponse();
        response.setId(1L);
        response.setName("Action Updated");

        Mockito.when(genreService.updateGenre(any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/genres/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Action Updated"));
    }

    @Test
    void deleteGenre_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/genres/1"))
                .andExpect(status().isNoContent());
    }
}
