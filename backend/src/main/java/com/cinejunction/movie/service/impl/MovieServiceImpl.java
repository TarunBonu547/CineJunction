package com.cinejunction.movie.service.impl;

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
import com.cinejunction.movie.service.MovieService;
import com.cinejunction.movie.specification.MovieSpecification;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for managing movies.
 */
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final MovieMapper movieMapper;

    @Override
    @Transactional
    public MovieResponse createMovie(MovieRequest request) {
        if (movieRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new MovieNotFoundException("Movie already exists with title: " + request.getTitle());
        }

        Set<Genre> genres = new HashSet<>();
        if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
            genres = new HashSet<>(genreRepository.findAllById(request.getGenreIds()));
            if (genres.size() != request.getGenreIds().size()) {
                throw new MovieNotFoundException("One or more genre IDs are invalid");
            }
        }

        Movie movie = movieMapper.toEntity(request);
        movie.setGenres(genres);
        movieRepository.save(movie);

        return buildMovieResponse(movie);
    }

    @Override
    @Transactional
    public MovieResponse updateMovie(Long id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));

        if (!movie.getTitle().equalsIgnoreCase(request.getTitle())
                && movieRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new MovieNotFoundException("Movie already exists with title: " + request.getTitle());
        }

        movieMapper.updateMovieFromRequest(request, movie);

        if (request.getGenreIds() != null) {
            Set<Genre> genres = new HashSet<>(genreRepository.findAllById(request.getGenreIds()));
            if (genres.size() != request.getGenreIds().size()) {
                throw new MovieNotFoundException("One or more genre IDs are invalid");
            }
            movie.setGenres(genres);
        }

        movieRepository.save(movie);
        return buildMovieResponse(movie);
    }

    @Override
    @Transactional
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));
        movieRepository.delete(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id: " + id));
        return buildMovieResponse(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieSummaryResponse> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable).map(movieMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieSummaryResponse> searchMovies(String keyword, Pageable pageable) {
        return movieRepository.findByTitleContainingIgnoreCase(keyword, pageable)
                .map(movieMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieSummaryResponse> getFilteredMovies(
            String genre,
            String language,
            Integer year,
            MovieStatus status,
            BigDecimal minRating,
            Integer maxRuntime,
            Boolean adult,
            Pageable pageable) {

        Specification<Movie> spec = MovieSpecification.withFilters(
                genre, language, year, status, minRating, maxRuntime, adult);

        return movieRepository.findAll(spec, pageable).map(movieMapper::toSummary);
    }

    private MovieResponse buildMovieResponse(Movie movie) {
        MovieResponse response = movieMapper.toResponse(movie);
        if (movie.getGenres() != null) {
            Set<String> genreNames = movie.getGenres().stream()
                    .map(Genre::getName)
                    .collect(Collectors.toSet());
            response.setGenres(genreNames);
        } else {
            response.setGenres(new HashSet<>());
        }
        return response;
    }
}
