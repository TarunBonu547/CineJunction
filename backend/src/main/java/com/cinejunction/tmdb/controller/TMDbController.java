package com.cinejunction.tmdb.controller;

import com.cinejunction.movie.dto.MovieResponse;
import com.cinejunction.movie.mapper.MovieMapper;
import com.cinejunction.movie.mapper.MovieMapperImpl;
import com.cinejunction.tmdb.dto.CreditsResponseDto;
import com.cinejunction.tmdb.dto.ImportCreditsSummaryResponse;
import com.cinejunction.tmdb.dto.ImportMovieMetadataResponse;
import com.cinejunction.tmdb.dto.ImportMovieResponse;
import com.cinejunction.tmdb.dto.MovieDetailsDto;
import com.cinejunction.tmdb.importer.MovieCreditsImporter;
import com.cinejunction.tmdb.importer.MovieImporter;
import com.cinejunction.tmdb.importer.MovieMetadataImporter;
import com.cinejunction.tmdb.importer.MovieMetadataImporter;
import com.cinejunction.tmdb.service.TMDbService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tmdb")
@RequiredArgsConstructor
@Tag(name = "TMDb", description = "TMDb integration APIs")
public class TMDbController {

    private final TMDbService tmDbService;
    private final MovieImporter movieImporter;
    private final MovieCreditsImporter movieCreditsImporter;
    private final MovieMetadataImporter movieMetadataImporter;
    private final MovieMapper movieMapper;

    @GetMapping("/movie/{id}")
    @Operation(summary = "Get movie details from TMDb", description = "Retrieves movie details from TMDb by movie ID", parameters = {
            @Parameter(name = "id", description = "TMDb movie ID", required = true, in = ParameterIn.PATH, schema = @Schema(type = "integer", format = "int64", example = "27205"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie details retrieved successfully", content = @Content(schema = @Schema(implementation = MovieDetailsDto.class))),
            @ApiResponse(responseCode = "404", description = "Movie not found on TMDb"),
            @ApiResponse(responseCode = "401", description = "Invalid TMDb API key"),
            @ApiResponse(responseCode = "429", description = "TMDb rate limit exceeded"),
            @ApiResponse(responseCode = "500", description = "TMDb server error")
    })
    public ResponseEntity<MovieDetailsDto> getMovie(@PathVariable Long id) {
        MovieDetailsDto movie = tmDbService.getMovie(id);
        return ResponseEntity.ok(movie);
    }

    @GetMapping("/movie/{movieId}/credits")
    @Operation(summary = "Get movie credits from TMDb", description = "Retrieves cast and crew information from TMDb by movie ID", parameters = {
            @Parameter(name = "movieId", description = "TMDb movie ID", required = true, in = ParameterIn.PATH, schema = @Schema(type = "integer", format = "int64", example = "155"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie credits retrieved successfully", content = @Content(schema = @Schema(implementation = CreditsResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Movie not found on TMDb"),
            @ApiResponse(responseCode = "401", description = "Invalid TMDb API key"),
            @ApiResponse(responseCode = "429", description = "TMDb rate limit exceeded"),
            @ApiResponse(responseCode = "500", description = "TMDb server error")
    })
    public ResponseEntity<CreditsResponseDto> getMovieCredits(@PathVariable Long movieId) {
        CreditsResponseDto credits = tmDbService.getMovieCredits(movieId);
        return ResponseEntity.ok(credits);
    }

    @PostMapping("/import/movie/{tmdbMovieId}/metadata")
    @Operation(summary = "Import movie metadata from TMDb", description = "Fetches extended metadata (companies, countries, languages, keywords, collection) from TMDb and imports them into the CineJunction database", parameters = {
            @Parameter(name = "tmdbMovieId", description = "TMDb movie ID to import metadata for", required = true, in = ParameterIn.PATH, schema = @Schema(type = "integer", format = "int64", example = "155"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie metadata imported successfully", content = @Content(schema = @Schema(implementation = ImportMovieMetadataResponse.class))),
            @ApiResponse(responseCode = "404", description = "Movie not found on TMDb or not imported locally")
    })
    public ResponseEntity<ImportMovieMetadataResponse> importMovieMetadata(@PathVariable Long tmdbMovieId) {
        ImportMovieMetadataResponse summary = movieMetadataImporter.importMetadata(tmdbMovieId);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/import/movie/{tmdbMovieId}/credits")
    @Operation(summary = "Import movie credits from TMDb", description = "Fetches cast and crew from TMDb and imports them into the CineJunction database", parameters = {
            @Parameter(name = "tmdbMovieId", description = "TMDb movie ID to import credits for", required = true, in = ParameterIn.PATH, schema = @Schema(type = "integer", format = "int64", example = "155"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie credits imported successfully", content = @Content(schema = @Schema(implementation = ImportCreditsSummaryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Movie not found on TMDb or not imported locally")
    })
    public ResponseEntity<ImportCreditsSummaryResponse> importMovieCredits(@PathVariable Long tmdbMovieId) {
        ImportCreditsSummaryResponse summary = movieCreditsImporter.importCredits(tmdbMovieId);
        return ResponseEntity.ok(summary);
    }

    @PostMapping("/import/movie/{tmdbMovieId}")
    @Operation(summary = "Import movie from TMDb", description = "Fetches a movie from TMDb and imports it into the CineJunction database", parameters = {
            @Parameter(name = "tmdbMovieId", description = "TMDb movie ID to import", required = true, in = ParameterIn.PATH, schema = @Schema(type = "integer", format = "int64", example = "27205"))
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movie imported successfully", content = @Content(schema = @Schema(implementation = ImportMovieResponse.class))),
            @ApiResponse(responseCode = "404", description = "Movie not found on TMDb"),
            @ApiResponse(responseCode = "409", description = "Movie already imported")
    })
    public ResponseEntity<ImportMovieResponse> importMovie(@PathVariable Long tmdbMovieId) {
        com.cinejunction.movie.entity.Movie imported = movieImporter.importMovie(tmdbMovieId);
        ImportMovieResponse response = ImportMovieResponse.builder()
                .id(imported.getId())
                .title(imported.getTitle())
                .overview(imported.getOverview())
                .releaseDate(imported.getReleaseDate())
                .runtime(imported.getRuntime())
                .language(imported.getLanguage())
                .posterUrl(imported.getPosterUrl())
                .backdropUrl(imported.getBackdropUrl())
                .trailerUrl(imported.getTrailerUrl())
                .status(imported.getStatus() != null ? imported.getStatus().name() : null)
                .adult(imported.isAdult())
                .budget(imported.getBudget())
                .revenue(imported.getRevenue())
                .averageRating(imported.getAverageRating())
                .voteCount(imported.getVoteCount())
                .popularity(imported.getPopularity())
                .genres(imported.getGenres() != null ? imported.getGenres().stream().map(com.cinejunction.genre.entity.Genre::getName).collect(java.util.stream.Collectors.toSet()) : null)
                .message("Movie imported successfully")
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
