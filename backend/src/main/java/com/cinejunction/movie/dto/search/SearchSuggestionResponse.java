package com.cinejunction.movie.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Search suggestion response DTO for autocomplete.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchSuggestionResponse {

    private String title;
    private String posterUrl;
    private Integer releaseYear;
}
