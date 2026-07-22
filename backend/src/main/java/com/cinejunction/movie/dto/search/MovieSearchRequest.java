package com.cinejunction.movie.dto.search;

import com.cinejunction.movie.enums.MovieStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Request DTO for advanced movie search.
 */
@Getter
@Setter
@NoArgsConstructor
public class MovieSearchRequest {

    @Size(max = 100, message = "Search keyword must not exceed 100 characters")
    private String keyword;

    @Size(max = 50, message = "Genre must not exceed 50 characters")
    private String genre;

    @Size(max = 50, message = "Language must not exceed 50 characters")
    private String language;

    @Min(value = 1888, message = "Release year must be >= 1888")
    @Max(value = 2100, message = "Release year must be <= 2100")
    private Integer year;

    @DecimalMin(value = "0.0", message = "Minimum rating must be >= 0.0")
    @DecimalMax(value = "10.0", message = "Minimum rating must be <= 10.0")
    private BigDecimal minRating;

    @DecimalMin(value = "0.0", message = "Maximum rating must be >= 0.0")
    @DecimalMax(value = "10.0", message = "Maximum rating must be <= 10.0")
    private BigDecimal maxRating;

    @Min(value = 1, message = "Minimum runtime must be >= 1")
    private Integer minRuntime;

    @Max(value = 600, message = "Maximum runtime must be <= 600")
    private Integer maxRuntime;

    private MovieStatus status;

    private Boolean adult;

    @Pattern(regexp = "^(title|releaseDate|averageRating|popularity|runtime)$", message = "Invalid sort field")
    private String sortBy = "popularity";

    @Pattern(regexp = "^(asc|desc)$", message = "Sort direction must be asc or desc")
    private String sortDirection = "desc";

    @Min(value = 1, message = "Page number must be >= 1")
    private Integer page = 1;

    @Min(value = 1, message = "Page size must be >= 1")
    @Max(value = 50, message = "Page size must be <= 50")
    private Integer size = 10;
}
