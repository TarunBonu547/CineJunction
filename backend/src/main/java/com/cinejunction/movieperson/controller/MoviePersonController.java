package com.cinejunction.movieperson.controller;

import com.cinejunction.movieperson.dto.MoviePersonRequest;
import com.cinejunction.movieperson.dto.MoviePersonResponse;
import com.cinejunction.movieperson.dto.MoviePersonSummaryResponse;
import com.cinejunction.movieperson.service.MoviePersonService;
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
@RequestMapping("/api/v1/movie-people")
@RequiredArgsConstructor
@Tag(name = "Movie People", description = "Movie-People relationship management APIs")
@SecurityRequirement(name = "bearerAuth")
public class MoviePersonController {

    private final MoviePersonService moviePersonService;

    @PostMapping
    @Operation(summary = "Assign person to movie", description = "Creates a relationship between a movie and a person with a specific role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Person assigned to movie successfully", content = @Content(schema = @Schema(implementation = MoviePersonResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Movie or person not found"),
            @ApiResponse(responseCode = "409", description = "Relationship already exists")
    })
    public ResponseEntity<MoviePersonResponse> assignPersonToMovie(@Valid @RequestBody MoviePersonRequest request) {
        MoviePersonResponse response = moviePersonService.assignPersonToMovie(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/movie/{movieId}")
    @Operation(summary = "Get movie cast", description = "Retrieves all actors for a specific movie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie cast retrieved successfully", content = @Content(schema = @Schema(implementation = MoviePersonSummaryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<List<MoviePersonSummaryResponse>> getMovieCast(@PathVariable Long movieId) {
        List<MoviePersonSummaryResponse> cast = moviePersonService.getMovieCast(movieId);
        return ResponseEntity.ok(cast);
    }

    @GetMapping("/person/{personId}")
    @Operation(summary = "Get person filmography", description = "Retrieves all movies for a specific person with pagination", parameters = {
            @Parameter(name = "page", description = "Page number (0-based)", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", description = "Sorting criteria in the format: field,asc or field,desc", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "billingOrder,asc"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person filmography retrieved successfully", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    public ResponseEntity<Page<MoviePersonSummaryResponse>> getPersonFilmography(
            @PathVariable Long personId,
            @Parameter(hidden = true) Pageable pageable) {
        Page<MoviePersonSummaryResponse> filmography = moviePersonService.getPersonFilmography(personId, pageable);
        return ResponseEntity.ok(filmography);
    }

    @GetMapping("/movie/{movieId}/crew")
    @Operation(summary = "Get movie crew", description = "Retrieves all non-actor crew members for a specific movie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie crew retrieved successfully", content = @Content(schema = @Schema(implementation = MoviePersonSummaryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<List<MoviePersonSummaryResponse>> getMovieCrew(@PathVariable Long movieId) {
        List<MoviePersonSummaryResponse> crew = moviePersonService.getMovieCrew(movieId);
        return ResponseEntity.ok(crew);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update relationship", description = "Updates an existing movie-person relationship")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relationship updated successfully", content = @Content(schema = @Schema(implementation = MoviePersonResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Relationship, movie, or person not found"),
            @ApiResponse(responseCode = "409", description = "Relationship already exists")
    })
    public ResponseEntity<MoviePersonResponse> updateRelationship(@PathVariable Long id, @Valid @RequestBody MoviePersonRequest request) {
        MoviePersonResponse response = moviePersonService.updateRelationship(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete relationship", description = "Deletes a movie-person relationship by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Relationship deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Relationship not found")
    })
    public ResponseEntity<Void> deleteRelationship(@PathVariable Long id) {
        moviePersonService.deleteRelationship(id);
        return ResponseEntity.noContent().build();
    }
}
