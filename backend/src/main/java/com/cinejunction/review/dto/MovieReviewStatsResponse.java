package com.cinejunction.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieReviewStatsResponse {

    private Long movieId;
    private String movieTitle;
    private Long totalReviews;
    private Long spoilerReviews;
    private Long nonSpoilerReviews;
}
