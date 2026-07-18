package com.cinejunction.movie.controller;

import com.cinejunction.movie.dto.MovieRequest;
import com.cinejunction.movie.dto.MovieResponse;
import com.cinejunction.movie.dto.MovieSummaryResponse;
import com.cinejunction.movie.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Tag(name = "Movies", description = "Movie management APIs")
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    @Operation(summary = "Create a new movie", description = "Creates a new movie with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movie created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or duplicate title"),
            @ApiResponse(responseCode = "404", description = "One or more genre IDs not found")
    })
    public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieRequest request) {
        MovieResponse response = movieService.createMovie(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all movies", description = "Retrieves all movies with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movies retrieved successfully")
    })
    public ResponseEntity<Page<MovieSummaryResponse>> getAllMovies(Pageable pageable) {
        Page<MovieSummaryResponse> movies = movieService.getAllMovies(pageable);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get movie by ID", description = "Retrieves a movie by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie found"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable Long id) {
        MovieResponse response = movieService.getMovieById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a movie", description = "Updates an existing movie by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie updated successfully"),
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
    @Operation(summary = "Search movies", description = "Searches movies by title keyword with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    })
    public ResponseEntity<Page<MovieSummaryResponse>> searchMovies(
            @RequestParam String keyword,
            Pageable pageable) {
        Page<MovieSummaryResponse> movies = movieService.searchMovies(keyword, pageable);
        return ResponseEntity.ok(movies);
    }
}
