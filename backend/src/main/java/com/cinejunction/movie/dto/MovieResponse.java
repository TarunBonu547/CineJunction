package com.cinejunction.movie.dto;

import com.cinejunction.movie.enums.MovieStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

/**
 * Detailed response DTO for movie details.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieResponse {

    private Long id;
    private String title;
    private String overview;
    private LocalDate releaseDate;
    private Integer runtime;
    private String language;
    private String posterUrl;
    private String backdropUrl;
    private String trailerUrl;
    private MovieStatus status;
    private Boolean adult;
    private Long budget;
    private Long revenue;
    private BigDecimal averageRating;
    private Integer voteCount;
    private BigDecimal popularity;
    private Set<String> genres;
}
