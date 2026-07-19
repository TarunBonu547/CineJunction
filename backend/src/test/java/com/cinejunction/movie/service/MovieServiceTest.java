package com.cinejunction.movie.service;

import com.cinejunction.exception.MovieNotFoundException;
import com.cinejunction.genre.entity.Genre;
import com.cinejunction.genre.repository.GenreRepository;
import com.cinejunction.movie.dto.MovieRequest;
import com.cinejunction.movie.dto.MovieResponse;
import com.cinejunction.movie.dto.MovieSummaryResponse;
import com.cinejunction.movie.entity.Movie;
import com.cinejunction.movie.enums.MovieStatus;
import com.cinejunction.movie.mapper.MovieMapper;
import com.cinejunction.movie.repository.MovieRepository;
import com.cinejunction.movie.service.impl.MovieServiceImpl;
import com.cinejunction.movie.specification.MovieSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie movie;
    private MovieRequest movieRequest;
    private Genre genre;

    @BeforeEach
    void setUp() {
        movie = Movie.builder()
                .id(1L)
                .title("Inception")
                .overview("A thief who steals corporate secrets through dream-sharing technology")
                .language("English")
                .status(MovieStatus.RELEASED)
                .adult(false)
                .runtime(148)
                .budget(160000000L)
                .revenue(829895144L)
                .averageRating(new BigDecimal("8.8"))
                .voteCount(20000)
                .popularity(new BigDecimal("85.5"))
                .genres(new HashSet<>())
                .build();

        movieRequest = new MovieRequest();
        movieRequest.setTitle("Inception");
        movieRequest.setOverview("A thief who steals corporate secrets through dream-sharing technology");
        movieRequest.setLanguage("English");
        movieRequest.setStatus(MovieStatus.RELEASED);
        movieRequest.setAdult(false);
        movieRequest.setRuntime(148);
        movieRequest.setBudget(160000000L);
        movieRequest.setRevenue(829895144L);
        movieRequest.setGenreIds(Set.of(1L));

        genre = new Genre();
        genre.setId(1L);
        genre.setName("Sci-Fi");
    }

    @Test
    void createMovie_Success() {
        when(movieRepository.existsByTitleIgnoreCase("Inception")).thenReturn(false);
        when(genreRepository.findAllById(any())).thenReturn(List.of(genre));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        when(movieMapper.toResponse(any())).thenReturn(new MovieResponse());
        when(movieMapper.toEntity(any())).thenReturn(movie);

        MovieResponse response = movieService.createMovie(movieRequest);

        assertThat(response).isNotNull();
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void createMovie_DuplicateTitle_ThrowsException() {
        when(movieRepository.existsByTitleIgnoreCase("Inception")).thenReturn(true);

        assertThrows(MovieNotFoundException.class, () -> movieService.createMovie(movieRequest));
    }

    @Test
    void getMovieById_Success() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieMapper.toResponse(any())).thenReturn(new MovieResponse());

        MovieResponse response = movieService.getMovieById(1L);

        assertThat(response).isNotNull();
    }

    @Test
    void getMovieById_NotFound_ThrowsException() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> movieService.getMovieById(1L));
    }

    @Test
    void updateMovie_Success() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(genreRepository.findAllById(any())).thenReturn(List.of(genre));
        when(movieRepository.save(any())).thenReturn(movie);
        when(movieMapper.toResponse(any())).thenReturn(new MovieResponse());

        MovieResponse response = movieService.updateMovie(1L, movieRequest);

        assertThat(response).isNotNull();
        verify(movieRepository).save(movie);
    }

    @Test
    void updateMovie_NotFound_ThrowsException() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> movieService.updateMovie(1L, movieRequest));
    }

    @Test
    void deleteMovie_Success() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        movieService.deleteMovie(1L);

        verify(movieRepository).delete(movie);
    }

    @Test
    void deleteMovie_NotFound_ThrowsException() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> movieService.deleteMovie(1L));
    }

    @Test
    void getAllMovies_ReturnsPage() {
        Page<Movie> moviePage = new PageImpl<>(List.of(movie));
        when(movieRepository.findAll(any(Pageable.class))).thenReturn(moviePage);
        when(movieMapper.toSummary(any())).thenReturn(new MovieSummaryResponse());

        Page<MovieSummaryResponse> result = movieService.getAllMovies(Pageable.unpaged());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void searchMovies_ReturnsFilteredPage() {
        Page<Movie> moviePage = new PageImpl<>(List.of(movie));
        when(movieRepository.findByTitleContainingIgnoreCase(eq("Inception"), any(Pageable.class))).thenReturn(moviePage);
        when(movieMapper.toSummary(any())).thenReturn(new MovieSummaryResponse());

        Page<MovieSummaryResponse> result = movieService.searchMovies("Inception", Pageable.unpaged());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getFilteredMovies_WithFilters_ReturnsFilteredPage() {
        Page<Movie> moviePage = new PageImpl<>(List.of(movie));
        when(movieRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(Pageable.class))).thenReturn(moviePage);
        when(movieMapper.toSummary(any())).thenReturn(new MovieSummaryResponse());

        Page<MovieSummaryResponse> result = movieService.getFilteredMovies("Sci-Fi", "English", 2010, MovieStatus.RELEASED, new BigDecimal("8.0"), 150, false, Pageable.unpaged());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }
}
