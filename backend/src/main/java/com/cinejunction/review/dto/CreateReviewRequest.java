package com.cinejunction.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateReviewRequest {

    @NotNull(message = "Movie ID is required")
    private Long movieId;

    @NotBlank(message = "Review title is required")
    @Size(min = 5, max = 150, message = "Review title must be between 5 and 150 characters")
    private String title;

    @NotBlank(message = "Review text is required")
    @Size(min = 20, max = 5000, message = "Review text must be between 20 and 5000 characters")
    private String reviewText;

    private Boolean containsSpoilers = false;
}
