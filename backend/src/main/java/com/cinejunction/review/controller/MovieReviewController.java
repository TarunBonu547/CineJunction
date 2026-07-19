package com.cinejunction.review.controller;

import com.cinejunction.review.dto.CreateReviewRequest;
import com.cinejunction.review.dto.MovieReviewStatsResponse;
import com.cinejunction.review.dto.ReviewResponse;
import com.cinejunction.review.dto.UpdateReviewRequest;
import com.cinejunction.review.service.MovieReviewService;
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

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Movie review management APIs")
@SecurityRequirement(name = "bearerAuth")
public class MovieReviewController {

    private final MovieReviewService movieReviewService;

    @PostMapping
    @Operation(summary = "Create a review", description = "Creates a new review for a movie by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created successfully", content = @Content(schema = @Schema(implementation = ReviewResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "409", description = "User has already reviewed this movie")
    })
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody CreateReviewRequest request) {
        ReviewResponse response = movieReviewService.createReview(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a review", description = "Updates an existing review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review updated successfully", content = @Content(schema = @Schema(implementation = ReviewResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to update this review"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewRequest request) {
        ReviewResponse response = movieReviewService.updateReview(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a review", description = "Deletes a review by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to delete this review"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        movieReviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID", description = "Retrieves a review by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review found", content = @Content(schema = @Schema(implementation = ReviewResponse.class))),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long id) {
        ReviewResponse response = movieReviewService.getReview(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/movie/{movieId}")
    @Operation(summary = "Get movie reviews", description = "Retrieves all reviews for a specific movie with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie reviews retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<Page<ReviewResponse>> getMovieReviews(
            @PathVariable Long movieId,
            @Parameter(hidden = true) Pageable pageable) {
        Page<ReviewResponse> reviews = movieReviewService.getMovieReviews(movieId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user reviews", description = "Retrieves all reviews by a specific user with pagination", parameters = {
            @Parameter(name = "page", description = "Page number (0-based)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", description = "Sorting criteria in the format: field,asc or field,desc", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "createdAt,desc"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User reviews retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Page<ReviewResponse>> getUserReviews(
            @PathVariable Long userId,
            @Parameter(hidden = true) Pageable pageable) {
        Page<ReviewResponse> reviews = movieReviewService.getUserReviews(userId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/movie/{movieId}/stats")
    @Operation(summary = "Get movie review statistics", description = "Retrieves review statistics for a specific movie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie review statistics retrieved successfully", content = @Content(schema = @Schema(implementation = MovieReviewStatsResponse.class))),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<MovieReviewStatsResponse> getMovieReviewStats(@PathVariable Long movieId) {
        MovieReviewStatsResponse stats = movieReviewService.getMovieReviewStats(movieId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/search")
    @Operation(summary = "Search reviews", description = "Searches reviews by title or content", parameters = {
            @Parameter(name = "keyword", description = "Search keyword", required = true, in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "page", description = "Page number (0-based)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", description = "Sorting criteria", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "createdAt,desc"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Keyword is required")
    })
    public ResponseEntity<Page<ReviewResponse>> searchReviews(
            @RequestParam String keyword,
            @Parameter(hidden = true) Pageable pageable) {
        Page<ReviewResponse> reviews = movieReviewService.searchReviews(keyword, pageable);
        return ResponseEntity.ok(reviews);
    }
}
