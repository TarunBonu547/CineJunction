package com.cinejunction.movieperson.controller;

import com.cinejunction.exception.GlobalExceptionHandler;
import com.cinejunction.movieperson.dto.MoviePersonRequest;
import com.cinejunction.movieperson.dto.MoviePersonResponse;
import com.cinejunction.movieperson.dto.MoviePersonSummaryResponse;
import com.cinejunction.movieperson.service.MoviePersonService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class MoviePersonControllerTest {

    private MockMvc mockMvc;
    private MoviePersonService moviePersonService;
    private ObjectMapper objectMapper;
    private MoviePersonController moviePersonController;

    @BeforeEach
    void setUp() {
        moviePersonService = Mockito.mock(MoviePersonService.class);
        moviePersonController = new MoviePersonController(moviePersonService);
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(moviePersonController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void assignPersonToMovie_ReturnsCreated() throws Exception {
        MoviePersonRequest request = new MoviePersonRequest();
        request.setMovieId(1L);
        request.setPersonId(2L);
        request.setRoleType(com.cinejunction.movieperson.enums.RoleType.DIRECTOR);
        request.setBillingOrder(0);

        MoviePersonResponse response = new MoviePersonResponse();
        response.setRelationshipId(1L);
        response.setMovieId(1L);
        response.setMovieTitle("Interstellar");
        response.setPersonId(2L);
        response.setPersonName("Christopher Nolan");
        response.setRoleType(com.cinejunction.movieperson.enums.RoleType.DIRECTOR);

        Mockito.when(moviePersonService.assignPersonToMovie(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/movie-people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.relationshipId").value(1))
                .andExpect(jsonPath("$.movieTitle").value("Interstellar"));
    }

    @Test
    void assignPersonToMovie_InvalidRequest_ReturnsBadRequest() throws Exception {
        MoviePersonRequest request = new MoviePersonRequest();
        request.setMovieId(null);
        request.setPersonId(null);
        request.setRoleType(null);
        request.setBillingOrder(null);

        mockMvc.perform(post("/api/v1/movie-people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMovieCast_ReturnsList() throws Exception {
        MoviePersonSummaryResponse summary = new MoviePersonSummaryResponse();
        summary.setRelationshipId(1L);
        summary.setPersonName("Matthew McConaughey");
        summary.setRoleType(com.cinejunction.movieperson.enums.RoleType.ACTOR);

        Mockito.when(moviePersonService.getMovieCast(1L)).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/v1/movie-people/movie/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].personName").value("Matthew McConaughey"));
    }

    @Test
    void getMovieCrew_ReturnsList() throws Exception {
        MoviePersonSummaryResponse summary = new MoviePersonSummaryResponse();
        summary.setRelationshipId(2L);
        summary.setPersonName("Christopher Nolan");
        summary.setRoleType(com.cinejunction.movieperson.enums.RoleType.DIRECTOR);

        Mockito.when(moviePersonService.getMovieCrew(1L)).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/v1/movie-people/movie/1/crew"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].personName").value("Christopher Nolan"));
    }

    @Test
    void getPersonFilmography_ReturnsPage() throws Exception {
        MoviePersonSummaryResponse summary = new MoviePersonSummaryResponse();
        summary.setRelationshipId(1L);
        summary.setRoleType(com.cinejunction.movieperson.enums.RoleType.DIRECTOR);

        Page<MoviePersonSummaryResponse> page = new PageImpl<>(List.of(summary), PageRequest.of(0, 1), 1);
        Mockito.when(moviePersonService.getPersonFilmography(eq(2L), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/movie-people/person/2")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].relationshipId").value(1));
    }

    @Test
    void updateRelationship_ReturnsUpdated() throws Exception {
        MoviePersonRequest request = new MoviePersonRequest();
        request.setMovieId(1L);
        request.setPersonId(2L);
        request.setRoleType(com.cinejunction.movieperson.enums.RoleType.WRITER);
        request.setBillingOrder(1);

        MoviePersonResponse response = new MoviePersonResponse();
        response.setRelationshipId(1L);
        response.setRoleType(com.cinejunction.movieperson.enums.RoleType.WRITER);

        Mockito.when(moviePersonService.updateRelationship(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/movie-people/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleType").value("WRITER"));
    }

    @Test
    void deleteRelationship_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/movie-people/1"))
                .andExpect(status().isNoContent());
    }
}
