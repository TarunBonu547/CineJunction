package com.cinejunction.movie.service;

import com.cinejunction.movie.dto.MovieRequest;
import com.cinejunction.movie.dto.MovieResponse;
import com.cinejunction.movie.dto.MovieSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing movies.
 */
public interface MovieService {

    /**
     * Creates a new movie.
     *
     * @param request the movie creation request
     * @return the created movie response
     */
    MovieResponse createMovie(MovieRequest request);

    /**
     * Updates an existing movie by ID.
     *
     * @param id      the movie ID
     * @param request the movie update request
     * @return the updated movie response
     */
    MovieResponse updateMovie(Long id, MovieRequest request);

    /**
     * Deletes a movie by ID.
     *
     * @param id the movie ID
     */
    void deleteMovie(Long id);

    /**
     * Retrieves a movie by its ID.
     *
     * @param id the movie ID
     * @return the movie response
     */
    MovieResponse getMovieById(Long id);

    /**
     * Retrieves all movies with pagination.
     *
     * @param pageable pagination information
     * @return a page of movie summary responses
     */
    Page<MovieSummaryResponse> getAllMovies(Pageable pageable);

    /**
     * Searches movies by title keyword with pagination.
     *
     * @param keyword  the search keyword
     * @param pageable pagination information
     * @return a page of movie summary responses
     */
    Page<MovieSummaryResponse> searchMovies(String keyword, Pageable pageable);
}
