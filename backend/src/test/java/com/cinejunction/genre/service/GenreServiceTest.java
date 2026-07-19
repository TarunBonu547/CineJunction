package com.cinejunction.genre.service;

import com.cinejunction.exception.GenreAlreadyExistsException;
import com.cinejunction.exception.GenreNotFoundException;
import com.cinejunction.genre.dto.GenreRequest;
import com.cinejunction.genre.dto.GenreResponse;
import com.cinejunction.genre.entity.Genre;
import com.cinejunction.genre.mapper.GenreMapper;
import com.cinejunction.genre.repository.GenreRepository;
import com.cinejunction.genre.service.impl.GenreServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private GenreMapper genreMapper;

    @InjectMocks
    private GenreServiceImpl genreService;

    private Genre genre;
    private GenreRequest genreRequest;
    private GenreResponse genreResponse;

    @BeforeEach
    void setUp() {
        genre = new Genre();
        genre.setId(1L);
        genre.setName("Action");

        genreRequest = new GenreRequest();
        genreRequest.setName("Action");

        genreResponse = new GenreResponse();
        genreResponse.setId(1L);
        genreResponse.setName("Action");
    }

    @Test
    void createGenre_Success() {
        when(genreRepository.existsByNameIgnoreCase("Action")).thenReturn(false);
        when(genreMapper.toEntity(any())).thenReturn(genre);
        when(genreRepository.save(any())).thenReturn(genre);
        when(genreMapper.toResponse(any())).thenReturn(genreResponse);

        GenreResponse response = genreService.createGenre(genreRequest);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Action");
        verify(genreRepository).save(any());
    }

    @Test
    void createGenre_DuplicateName_ThrowsException() {
        when(genreRepository.existsByNameIgnoreCase("Action")).thenReturn(true);

        assertThrows(GenreAlreadyExistsException.class, () -> genreService.createGenre(genreRequest));
    }

    @Test
    void getGenreById_Success() {
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
        when(genreMapper.toResponse(any())).thenReturn(genreResponse);

        GenreResponse response = genreService.getGenreById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Action");
    }

    @Test
    void getGenreById_NotFound_ThrowsException() {
        when(genreRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GenreNotFoundException.class, () -> genreService.getGenreById(1L));
    }

    @Test
    void getAllGenres_ReturnsPage() {
        Page<Genre> genrePage = new PageImpl<>(List.of(genre));
        when(genreRepository.findAll(any(Pageable.class))).thenReturn(genrePage);
        when(genreMapper.toResponse(any())).thenReturn(genreResponse);

        Page<GenreResponse> result = genreService.getAllGenres(Pageable.unpaged());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void updateGenre_Success() {
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
        when(genreRepository.save(any())).thenReturn(genre);
        when(genreMapper.toResponse(any())).thenReturn(genreResponse);

        GenreResponse response = genreService.updateGenre(1L, genreRequest);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Action");
        verify(genreRepository).save(genre);
    }

    @Test
    void updateGenre_NotFound_ThrowsException() {
        when(genreRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GenreNotFoundException.class, () -> genreService.updateGenre(1L, genreRequest));
    }

    @Test
    void updateGenre_DuplicateName_ThrowsException() {
        Genre existing = new Genre();
        existing.setId(2L);
        existing.setName("Comedy");

        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));
        when(genreRepository.findByNameIgnoreCase("Action")).thenReturn(Optional.of(existing));

        assertThrows(GenreAlreadyExistsException.class, () -> genreService.updateGenre(1L, genreRequest));
    }

    @Test
    void deleteGenre_Success() {
        genre.setMovies(new HashSet<>());
        when(genreRepository.findById(1L)).thenReturn(Optional.of(genre));

        genreService.deleteGenre(1L);

        verify(genreRepository).delete(genre);
    }

    @Test
    void deleteGenre_ReferencedByMovies_ThrowsException() {
        Genre genreWithMovies = new Genre();
        genreWithMovies.setId(1L);
        genreWithMovies.setName("Action");
        java.util.Set<com.cinejunction.movie.entity.Movie> movies = new java.util.HashSet<>();
        movies.add(new com.cinejunction.movie.entity.Movie());
        genreWithMovies.setMovies(movies);

        when(genreRepository.findById(1L)).thenReturn(Optional.of(genreWithMovies));

        assertThrows(IllegalArgumentException.class, () -> genreService.deleteGenre(1L));
    }

    @Test
    void deleteGenre_NotFound_ThrowsException() {
        when(genreRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GenreNotFoundException.class, () -> genreService.deleteGenre(1L));
    }
}
