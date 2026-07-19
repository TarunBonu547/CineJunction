package com.cinejunction.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {

    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long userId;
    private String username;
    private String title;
    private String reviewText;
    private Boolean containsSpoilers;
    private Instant createdAt;
    private Instant updatedAt;
}
