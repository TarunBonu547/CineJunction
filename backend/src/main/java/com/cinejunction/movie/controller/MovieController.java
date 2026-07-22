package com.cinejunction.movie.controller;

import com.cinejunction.movie.dto.search.MovieSearchRequest;
import com.cinejunction.movie.dto.search.SearchSuggestionResponse;
import com.cinejunction.movie.dto.MovieRequest;
import com.cinejunction.movie.dto.MovieResponse;
import com.cinejunction.movie.dto.MovieSummaryResponse;
import com.cinejunction.movie.enums.MovieStatus;
import com.cinejunction.movie.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
@Tag(name = "Movies", description = "Movie management and advanced search APIs")
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    @Operation(summary = "Create a new movie", description = "Creates a new movie with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movie created successfully", content = @Content(schema = @Schema(implementation = MovieResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or duplicate title"),
            @ApiResponse(responseCode = "404", description = "One or more genre IDs not found")
    })
    public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieRequest request) {
        MovieResponse response = movieService.createMovie(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all movies", description = "Retrieves all movies with pagination, sorting, and advanced filtering", parameters = {
            @Parameter(name = "page", description = "Page number (0-based)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", description = "Sorting criteria in the format: field,asc or field,desc", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "releaseDate,desc")),
            @Parameter(name = "genre", description = "Filter by genre name", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "language", description = "Filter by language", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "year", description = "Filter by release year", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "status", description = "Filter by movie status", in = ParameterIn.QUERY, schema = @Schema(type = "string", allowableValues = {"UPCOMING", "IN_PRODUCTION", "POST_PRODUCTION", "RELEASED", "CANCELLED"})),
            @Parameter(name = "minRating", description = "Filter by minimum average rating", in = ParameterIn.QUERY, schema = @Schema(type = "number", format = "decimal")),
            @Parameter(name = "maxRuntime", description = "Filter by maximum runtime in minutes", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "adult", description = "Filter by adult content flag", in = ParameterIn.QUERY, schema = @Schema(type = "boolean"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movies retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<MovieSummaryResponse>> getAllMovies(
            @Parameter(hidden = true) Pageable pageable,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) MovieStatus status,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) Integer maxRuntime,
            @RequestParam(required = false) Boolean adult) {
        Page<MovieSummaryResponse> movies = movieService.getFilteredMovies(genre, language, year, status, minRating, maxRuntime, adult, pageable);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get movie by ID", description = "Retrieves a movie by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie found", content = @Content(schema = @Schema(implementation = MovieResponse.class))),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable Long id) {
        MovieResponse response = movieService.getMovieById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a movie", description = "Updates an existing movie by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie updated successfully", content = @Content(schema = @Schema(implementation = MovieResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or duplicate title"),
            @ApiResponse(responseCode = "404", description = "Movie not found or genre ID not found")
    })
    public ResponseEntity<MovieResponse> updateMovie(@PathVariable Long id, @Valid @RequestBody MovieRequest request) {
        MovieResponse response = movieService.updateMovie(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a movie", description = "Deletes a movie by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movie deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search movies by title", description = "Searches movies by title keyword with pagination", parameters = {
            @Parameter(name = "keyword", description = "Search keyword for movie title", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "Inception")),
            @Parameter(name = "page", description = "Page number (0-based)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", description = "Sorting criteria in the format: field,asc or field,desc", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "title,asc"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<MovieSummaryResponse>> searchMovies(
            @RequestParam String keyword,
            @Parameter(hidden = true) Pageable pageable) {
        Page<MovieSummaryResponse> movies = movieService.searchMovies(keyword, pageable);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/search/advanced")
    @Operation(summary = "Advanced movie search", description = "Performs multi-criteria advanced search with filtering, sorting, and pagination", parameters = {
            @Parameter(name = "keyword", description = "Search in title and overview", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "genre", description = "Filter by genre name", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "language", description = "Filter by language", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "year", description = "Filter by release year", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "minRating", description = "Filter by minimum average rating (0.0 - 10.0)", in = ParameterIn.QUERY, schema = @Schema(type = "number", format = "decimal")),
            @Parameter(name = "maxRating", description = "Filter by maximum average rating (0.0 - 10.0)", in = ParameterIn.QUERY, schema = @Schema(type = "number", format = "decimal")),
            @Parameter(name = "minRuntime", description = "Filter by minimum runtime in minutes", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "maxRuntime", description = "Filter by maximum runtime in minutes", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "status", description = "Filter by movie status", in = ParameterIn.QUERY, schema = @Schema(type = "string", allowableValues = {"UPCOMING", "IN_PRODUCTION", "POST_PRODUCTION", "RELEASED", "CANCELLED"})),
            @Parameter(name = "adult", description = "Filter by adult content flag", in = ParameterIn.QUERY, schema = @Schema(type = "boolean")),
            @Parameter(name = "sortBy", description = "Sort field", in = ParameterIn.QUERY, schema = @Schema(type = "string", allowableValues = {"title", "releaseDate", "averageRating", "popularity", "runtime"}, defaultValue = "popularity")),
            @Parameter(name = "sortDirection", description = "Sort direction", in = ParameterIn.QUERY, schema = @Schema(type = "string", allowableValues = {"asc", "desc"}, defaultValue = "desc")),
            @Parameter(name = "page", description = "Page number (1-based)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "1")),
            @Parameter(name = "size", description = "Page size (max 50)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Advanced search results retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<MovieSummaryResponse>> advancedSearch(@Valid @ModelAttribute MovieSearchRequest request) {
        Page<MovieSummaryResponse> movies = movieService.advancedSearch(request);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/search/suggestions")
    @Operation(summary = "Search suggestions", description = "Returns autocomplete suggestions based on title and overview", parameters = {
            @Parameter(name = "keyword", description = "Search keyword", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "Incept")),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search suggestions retrieved successfully")
    })
    public ResponseEntity<List<SearchSuggestionResponse>> getSearchSuggestions(
            @RequestParam String keyword) {
        List<SearchSuggestionResponse> suggestions = movieService.getSearchSuggestions(keyword);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recently released movies", description = "Retrieves movies released within the last N months", parameters = {
            @Parameter(name = "months", description = "Number of months to look back", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "3")),
            @Parameter(name = "page", description = "Page number (0-based)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", description = "Sorting criteria in the format: field,asc or field,desc", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "releaseDate,desc"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recently released movies retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<MovieSummaryResponse>> getRecentlyReleased(
            @Parameter(description = "Number of months to look back", example = "3") @RequestParam(defaultValue = "3") int months,
            @Parameter(hidden = true) Pageable pageable) {
        Page<MovieSummaryResponse> movies = movieService.getRecentlyReleased(months, pageable);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/trending")
    @Operation(summary = "Get trending movies", description = "Retrieves movies sorted by popularity", parameters = {
            @Parameter(name = "page", description = "Page number (0-based)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", description = "Sorting criteria in the format: field,asc or field,desc", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "popularity,desc"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trending movies retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<MovieSummaryResponse>> getTrendingMovies(@Parameter(hidden = true) Pageable pageable) {
        Page<MovieSummaryResponse> movies = movieService.getTrendingMovies(pageable);
        return ResponseEntity.ok(movies);
    }
}
