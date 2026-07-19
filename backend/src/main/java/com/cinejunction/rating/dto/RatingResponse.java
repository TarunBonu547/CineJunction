package com.cinejunction.rating.dto;

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
public class RatingResponse {

    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long userId;
    private String username;
    private Integer rating;
    private Instant createdAt;
    private Instant updatedAt;
}
