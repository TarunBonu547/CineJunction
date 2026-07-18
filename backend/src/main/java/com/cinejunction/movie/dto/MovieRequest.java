package com.cinejunction.movie.dto;

import com.cinejunction.movie.enums.MovieStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

/**
 * Request DTO for creating and updating movies.
 */
@Getter
@Setter
@NoArgsConstructor
public class MovieRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String overview;

    private LocalDate releaseDate;

    @Positive(message = "Runtime must be a positive value")
    private Integer runtime;

    @NotBlank(message = "Language is required")
    private String language;

    private String posterUrl;

    private String backdropUrl;

    private String trailerUrl;

    private MovieStatus status;

    private Boolean adult;

    @PositiveOrZero(message = "Budget must be zero or positive")
    private Long budget;

    @PositiveOrZero(message = "Revenue must be zero or positive")
    private Long revenue;

    private BigDecimal averageRating;

    private Integer voteCount;

    private BigDecimal popularity;

    private Set<Long> genreIds;
}
