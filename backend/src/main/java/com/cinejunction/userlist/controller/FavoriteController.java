package com.cinejunction.userlist.controller;

import com.cinejunction.movie.dto.MovieSummaryResponse;
import com.cinejunction.userlist.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorites", description = "User favorite movies APIs")
@SecurityRequirement(name = "bearerAuth")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{movieId}")
    @Operation(summary = "Add movie to favorites", description = "Adds a movie to the authenticated user's favorites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movie added to favorites"),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "409", description = "Movie already in favorites")
    })
    public ResponseEntity<Void> addFavorite(@PathVariable Long movieId) {
        Long userId = getCurrentUserId();
        favoriteService.addFavorite(userId, movieId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{movieId}")
    @Operation(summary = "Remove movie from favorites", description = "Removes a movie from the authenticated user's favorites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movie removed from favorites"),
            @ApiResponse(responseCode = "404", description = "Movie or favorite not found")
    })
    public ResponseEntity<Void> removeFavorite(@PathVariable Long movieId) {
        Long userId = getCurrentUserId();
        favoriteService.removeFavorite(userId, movieId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get user's favorite movies", description = "Retrieves all favorite movies for the authenticated user with pagination", parameters = {
            @Parameter(name = "page", description = "Page number (0-based)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", description = "Sorting criteria in the format: field,asc or field,desc", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "createdAt,desc"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorite movies retrieved successfully")
    })
    public ResponseEntity<Page<MovieSummaryResponse>> getFavorites(
            @Parameter(hidden = true) Pageable pageable) {
        Long userId = getCurrentUserId();
        Page<MovieSummaryResponse> favorites = favoriteService.getFavorites(userId, pageable);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/{movieId}/exists")
    @Operation(summary = "Check if movie is favorited", description = "Checks whether a specific movie is in the authenticated user's favorites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check completed successfully")
    })
    public ResponseEntity<Boolean> isFavorited(@PathVariable Long movieId) {
        Long userId = getCurrentUserId();
        boolean exists = favoriteService.isFavorited(userId, movieId);
        return ResponseEntity.ok(exists);
    }

    private Long getCurrentUserId() {
        org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        com.cinejunction.security.service.CustomUserDetails userDetails =
                (com.cinejunction.security.service.CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser().getId();
    }
}
