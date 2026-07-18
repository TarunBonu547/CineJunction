package com.cinejunction.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Summary response DTO for movie listings.
 */
@Getter
@Builder
@AllArgsConstructor
public class MovieSummaryResponse {

    private Long id;
    private String title;
    private String posterUrl;
    private LocalDate releaseDate;
    private BigDecimal averageRating;
    private BigDecimal popularity;
}
