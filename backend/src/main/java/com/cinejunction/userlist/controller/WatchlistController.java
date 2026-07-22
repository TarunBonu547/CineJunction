package com.cinejunction.userlist.controller;

import com.cinejunction.movie.dto.MovieSummaryResponse;
import com.cinejunction.userlist.dto.request.WatchlistStatusUpdateRequest;
import com.cinejunction.userlist.service.WatchlistService;
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

import java.util.Map;

@RestController
@RequestMapping("/api/v1/watchlist")
@RequiredArgsConstructor
@Tag(name = "Watchlist", description = "User watchlist management APIs")
@SecurityRequirement(name = "bearerAuth")
public class WatchlistController {

    private final WatchlistService watchlistService;

    @PostMapping("/{movieId}")
    @Operation(summary = "Add movie to watchlist", description = "Adds a movie to the authenticated user's watchlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movie added to watchlist"),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "409", description = "Movie already in watchlist")
    })
    public ResponseEntity<Void> addToWatchlist(@PathVariable Long movieId) {
        Long userId = getCurrentUserId();
        watchlistService.addToWatchlist(userId, movieId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{movieId}")
    @Operation(summary = "Remove movie from watchlist", description = "Removes a movie from the authenticated user's watchlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movie removed from watchlist"),
            @ApiResponse(responseCode = "404", description = "Movie or watchlist entry not found")
    })
    public ResponseEntity<Void> removeFromWatchlist(@PathVariable Long movieId) {
        Long userId = getCurrentUserId();
        watchlistService.removeFromWatchlist(userId, movieId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get user's watchlist", description = "Retrieves all movies in the authenticated user's watchlist with pagination", parameters = {
            @Parameter(name = "page", description = "Page number (0-based)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", description = "Sorting criteria in the format: field,asc or field,desc", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "createdAt,desc"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Watchlist retrieved successfully")
    })
    public ResponseEntity<Page<MovieSummaryResponse>> getWatchlist(
            @Parameter(hidden = true) Pageable pageable) {
        Long userId = getCurrentUserId();
        Page<MovieSummaryResponse> watchlist = watchlistService.getWatchlist(userId, pageable);
        return ResponseEntity.ok(watchlist);
    }

    @PatchMapping("/{movieId}/watched")
    @Operation(summary = "Mark movie as watched/unwatched", description = "Updates the watched status of a movie in the authenticated user's watchlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Watched status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Movie or watchlist entry not found")
    })
    public ResponseEntity<Void> updateWatchedStatus(
            @PathVariable Long movieId,
            @Valid @RequestBody WatchlistStatusUpdateRequest request) {
        Long userId = getCurrentUserId();
        watchlistService.updateWatchedStatus(userId, movieId, request.isWatched());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{movieId}/status")
    @Operation(summary = "Get watchlist status for a movie", description = "Checks whether a specific movie is in the authenticated user's watchlist and its watched status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Watchlist status retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Movie or watchlist entry not found")
    })
    public ResponseEntity<Map<String, Object>> getWatchlistStatus(@PathVariable Long movieId) {
        Long userId = getCurrentUserId();
        Map<String, Object> status = watchlistService.getWatchlistStatus(userId, movieId);
        return ResponseEntity.ok(status);
    }

    private Long getCurrentUserId() {
        org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        com.cinejunction.security.service.CustomUserDetails userDetails =
                (com.cinejunction.security.service.CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser().getId();
    }
}
