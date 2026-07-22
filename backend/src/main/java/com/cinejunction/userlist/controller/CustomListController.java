package com.cinejunction.userlist.controller;

import com.cinejunction.movie.dto.MovieSummaryResponse;
import com.cinejunction.userlist.service.CustomListService;
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
@RequestMapping("/api/v1/lists")
@RequiredArgsConstructor
@Tag(name = "Custom Lists", description = "Custom movie list management APIs")
@SecurityRequirement(name = "bearerAuth")
public class CustomListController {

    private final CustomListService customListService;

    @PostMapping
    @Operation(summary = "Create a custom list", description = "Creates a new custom movie list for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "List created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "List with same name already exists for this user")
    })
    public ResponseEntity<com.cinejunction.userlist.dto.response.CustomListResponse> createList(
            @Parameter(description = "List name", required = true, schema = @Schema(type = "string")) @RequestParam String name,
            @Parameter(description = "List description", schema = @Schema(type = "string")) @RequestParam(required = false) String description,
            @Parameter(description = "Whether the list is public", schema = @Schema(type = "boolean")) @RequestParam(defaultValue = "false") boolean isPublic,
            @Parameter(description = "Cover image URL", schema = @Schema(type = "string")) @RequestParam(required = false) String coverImage) {
        Long userId = getCurrentUserId();
        com.cinejunction.userlist.dto.response.CustomListResponse response = customListService.createList(userId, name, description, isPublic, coverImage);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{listId}")
    @Operation(summary = "Update a custom list", description = "Updates an existing custom list owned by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "List not found"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to update this list")
    })
    public ResponseEntity<com.cinejunction.userlist.dto.response.CustomListResponse> updateList(
            @PathVariable Long listId,
            @Parameter(description = "List name", required = true, schema = @Schema(type = "string")) @RequestParam String name,
            @Parameter(description = "List description", schema = @Schema(type = "string")) @RequestParam(required = false) String description,
            @Parameter(description = "Whether the list is public", schema = @Schema(type = "boolean")) @RequestParam(defaultValue = "false") boolean isPublic,
            @Parameter(description = "Cover image URL", schema = @Schema(type = "string")) @RequestParam(required = false) String coverImage) {
        Long userId = getCurrentUserId();
        com.cinejunction.userlist.dto.response.CustomListResponse response = customListService.updateList(userId, listId, name, description, isPublic, coverImage);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{listId}")
    @Operation(summary = "Delete a custom list", description = "Deletes a custom list owned by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "List deleted successfully"),
            @ApiResponse(responseCode = "404", description = "List not found"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to delete this list")
    })
    public ResponseEntity<Void> deleteList(@PathVariable Long listId) {
        Long userId = getCurrentUserId();
        customListService.deleteList(userId, listId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get user's custom lists", description = "Retrieves all custom lists for the authenticated user with pagination", parameters = {
            @Parameter(name = "page", description = "Page number (0-based)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", description = "Sorting criteria in the format: field,asc or field,desc", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "updatedAt,desc"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User's lists retrieved successfully")
    })
    public ResponseEntity<Page<com.cinejunction.userlist.dto.response.CustomListResponse>> getUserLists(
            @Parameter(hidden = true) Pageable pageable) {
        Long userId = getCurrentUserId();
        Page<com.cinejunction.userlist.dto.response.CustomListResponse> lists = customListService.getUserLists(userId, pageable);
        return ResponseEntity.ok(lists);
    }

    @GetMapping("/public")
    @Operation(summary = "Get public custom lists", description = "Retrieves all public custom lists with pagination", parameters = {
            @Parameter(name = "page", description = "Page number (0-based)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", description = "Sorting criteria in the format: field,asc or field,desc", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "updatedAt,desc"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Public lists retrieved successfully")
    })
    public ResponseEntity<Page<com.cinejunction.userlist.dto.response.CustomListResponse>> getPublicLists(
            @Parameter(hidden = true) Pageable pageable) {
        Page<com.cinejunction.userlist.dto.response.CustomListResponse> lists = customListService.getPublicLists(pageable);
        return ResponseEntity.ok(lists);
    }

    @GetMapping("/{listId}")
    @Operation(summary = "Get custom list by ID", description = "Retrieves a custom list by its ID. Owner can access private lists, everyone can access public lists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "List not found"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to access this private list")
    })
    public ResponseEntity<com.cinejunction.userlist.dto.response.CustomListResponse> getListById(@PathVariable Long listId) {
        Long userId = getCurrentUserId();
        com.cinejunction.userlist.dto.response.CustomListResponse response = customListService.getListById(userId, listId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{listId}/movies/{movieId}")
    @Operation(summary = "Add movie to custom list", description = "Adds a movie to a custom list owned by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movie added to list successfully"),
            @ApiResponse(responseCode = "404", description = "List or movie not found"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to modify this list"),
            @ApiResponse(responseCode = "409", description = "Movie already exists in this list")
    })
    public ResponseEntity<Void> addMovieToList(@PathVariable Long listId, @PathVariable Long movieId) {
        Long userId = getCurrentUserId();
        customListService.addMovieToList(userId, listId, movieId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{listId}/movies/{movieId}")
    @Operation(summary = "Remove movie from custom list", description = "Removes a movie from a custom list owned by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movie removed from list successfully"),
            @ApiResponse(responseCode = "404", description = "List, movie, or list entry not found"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to modify this list")
    })
    public ResponseEntity<Void> removeMovieFromList(@PathVariable Long listId, @PathVariable Long movieId) {
        Long userId = getCurrentUserId();
        customListService.removeMovieFromList(userId, listId, movieId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{listId}/reorder")
    @Operation(summary = "Reorder movies in custom list", description = "Updates the order of movies in a custom list owned by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movies reordered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid movie IDs list"),
            @ApiResponse(responseCode = "404", description = "List not found"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to modify this list")
    })
    public ResponseEntity<Void> reorderMovies(
            @PathVariable Long listId,
            @Parameter(description = "Ordered list of movie IDs", required = true, schema = @Schema(type = "array", implementation = Long.class)) @RequestBody java.util.List<Long> movieIds) {
        Long userId = getCurrentUserId();
        customListService.reorderMovies(userId, listId, movieIds);
        return ResponseEntity.ok().build();
    }

    private Long getCurrentUserId() {
        org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        com.cinejunction.security.service.CustomUserDetails userDetails =
                (com.cinejunction.security.service.CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser().getId();
    }
}
