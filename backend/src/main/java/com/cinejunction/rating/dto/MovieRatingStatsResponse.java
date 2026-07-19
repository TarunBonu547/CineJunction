package com.cinejunction.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieRatingStatsResponse {

    private Long movieId;
    private String movieTitle;
    private Double averageRating;
    private Long totalRatings;
    private Map<Integer, Long> ratingDistribution;
}
