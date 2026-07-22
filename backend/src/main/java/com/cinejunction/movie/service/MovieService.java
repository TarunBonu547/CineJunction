package com.cinejunction.movie.service;

import com.cinejunction.movie.dto.search.MovieSearchRequest;
import com.cinejunction.movie.dto.search.SearchSuggestionResponse;
import com.cinejunction.movie.dto.MovieRequest;
import com.cinejunction.movie.dto.MovieResponse;
import com.cinejunction.movie.dto.MovieSummaryResponse;
import com.cinejunction.movie.enums.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    /**
     * Retrieves all movies with advanced filtering, sorting, and pagination.
     *
     * @param genre    the genre name filter
     * @param language the language filter
     * @param year     the release year filter
     * @param status   the movie status filter
     * @param minRating the minimum average rating filter
     * @param maxRuntime the maximum runtime filter
     * @param adult    the adult content flag filter
     * @param pageable pagination and sorting information
     * @return a page of movie summary responses
     */
    Page<MovieSummaryResponse> getFilteredMovies(
            String genre,
            String language,
            Integer year,
            MovieStatus status,
            BigDecimal minRating,
            Integer maxRuntime,
            Boolean adult,
            Pageable pageable);

    /**
     * Performs an advanced multi-filter search.
     *
     * @param request the search request containing all filters, sort, and pagination
     * @return a page of movie summary responses
     */
    Page<MovieSummaryResponse> advancedSearch(MovieSearchRequest request);

    /**
     * Returns autocomplete suggestions for the given keyword.
     *
     * @param keyword the search keyword
     * @return a list of search suggestions limited to 10 results
     */
    List<SearchSuggestionResponse> getSearchSuggestions(String keyword);

    /**
     * Retrieves recently released movies.
     *
     * @param monthsBack number of months to look back
     * @param pageable   pagination information
     * @return a page of movie summary responses
     */
    Page<MovieSummaryResponse> getRecentlyReleased(int monthsBack, Pageable pageable);

    /**
     * Retrieves trending movies sorted by popularity.
     *
     * @param pageable pagination information
     * @return a page of movie summary responses
     */
    Page<MovieSummaryResponse> getTrendingMovies(Pageable pageable);
}
