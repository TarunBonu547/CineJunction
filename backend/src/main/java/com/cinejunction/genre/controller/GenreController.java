package com.cinejunction.genre.controller;

import com.cinejunction.genre.dto.GenreRequest;
import com.cinejunction.genre.dto.GenreResponse;
import com.cinejunction.genre.service.GenreService;
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
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
@Tag(name = "Genres", description = "Genre management APIs")
@SecurityRequirement(name = "bearerAuth")
public class GenreController {

    private final GenreService genreService;

    @PostMapping
    @Operation(summary = "Create a new genre", description = "Creates a new genre with the provided name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Genre created successfully", content = @Content(schema = @Schema(implementation = GenreResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or duplicate genre name"),
            @ApiResponse(responseCode = "409", description = "Genre with the same name already exists")
    })
    public ResponseEntity<GenreResponse> createGenre(@Valid @RequestBody GenreRequest request) {
        GenreResponse response = genreService.createGenre(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all genres", description = "Retrieves all genres with pagination", parameters = {
            @Parameter(name = "page", description = "Page number (0-based)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", description = "Sorting criteria in the format: field,asc or field,desc", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "name,asc"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genres retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<GenreResponse>> getAllGenres(
            @Parameter(hidden = true) Pageable pageable) {
        Page<GenreResponse> genres = genreService.getAllGenres(pageable);
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get genre by ID", description = "Retrieves a genre by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genre found", content = @Content(schema = @Schema(implementation = GenreResponse.class))),
            @ApiResponse(responseCode = "404", description = "Genre not found")
    })
    public ResponseEntity<GenreResponse> getGenreById(@PathVariable Long id) {
        GenreResponse response = genreService.getGenreById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a genre", description = "Updates an existing genre by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genre updated successfully", content = @Content(schema = @Schema(implementation = GenreResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or duplicate genre name"),
            @ApiResponse(responseCode = "404", description = "Genre not found"),
            @ApiResponse(responseCode = "409", description = "Genre with the same name already exists")
    })
    public ResponseEntity<GenreResponse> updateGenre(@PathVariable Long id, @Valid @RequestBody GenreRequest request) {
        GenreResponse response = genreService.updateGenre(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a genre", description = "Deletes a genre by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Genre deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Genre not found"),
            @ApiResponse(responseCode = "400", description = "Cannot delete genre because it is referenced by movies")
    })
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
