package com.cinejunction.rating.controller;

import com.cinejunction.rating.dto.CreateRatingRequest;
import com.cinejunction.rating.dto.MovieRatingStatsResponse;
import com.cinejunction.rating.dto.RatingResponse;
import com.cinejunction.rating.dto.UpdateRatingRequest;
import com.cinejunction.rating.service.MovieRatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
@Tag(name = "Ratings", description = "Movie rating management APIs")
@SecurityRequirement(name = "bearerAuth")
public class MovieRatingController {

    private final MovieRatingService movieRatingService;

    @PostMapping
    @Operation(summary = "Rate a movie", description = "Creates a rating for a movie by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rating created successfully", content = @Content(schema = @Schema(implementation = RatingResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "409", description = "User has already rated this movie")
    })
    public ResponseEntity<RatingResponse> createRating(@Valid @RequestBody CreateRatingRequest request) {
        RatingResponse response = movieRatingService.createRating(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a rating", description = "Updates the rating value for an existing rating")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating updated successfully", content = @Content(schema = @Schema(implementation = RatingResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Rating not found"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to update this rating")
    })
    public ResponseEntity<RatingResponse> updateRating(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRatingRequest request) {
        RatingResponse response = movieRatingService.updateRating(id, request.getRating());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a rating", description = "Deletes a rating by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rating deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Rating not found"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to delete this rating")
    })
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        movieRatingService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/movie/{movieId}")
    @Operation(summary = "Get movie ratings", description = "Retrieves all ratings for a specific movie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie ratings retrieved successfully", content = @Content(schema = @Schema(implementation = RatingResponse.class))),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<List<RatingResponse>> getMovieRatings(@PathVariable Long movieId) {
        List<RatingResponse> ratings = movieRatingService.getMovieRatings(movieId);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user ratings", description = "Retrieves all ratings by a specific user with pagination", parameters = {
            @Parameter(name = "page", description = "Page number (0-based)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", description = "Sorting criteria in the format: field,asc or field,desc", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "createdAt,desc"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User ratings retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Page<RatingResponse>> getUserRatings(
            @PathVariable Long userId,
            @Parameter(hidden = true) Pageable pageable) {
        Page<RatingResponse> ratings = movieRatingService.getUserRatings(userId, pageable);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/movie/{movieId}/stats")
    @Operation(summary = "Get movie rating statistics", description = "Retrieves rating statistics for a specific movie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie rating statistics retrieved successfully", content = @Content(schema = @Schema(implementation = MovieRatingStatsResponse.class))),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<MovieRatingStatsResponse> getMovieRatingStats(@PathVariable Long movieId) {
        MovieRatingStatsResponse stats = movieRatingService.getMovieRatingStats(movieId);
        return ResponseEntity.ok(stats);
    }
}
