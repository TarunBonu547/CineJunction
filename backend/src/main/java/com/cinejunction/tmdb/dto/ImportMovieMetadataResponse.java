package com.cinejunction.tmdb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportMovieMetadataResponse {

    private Long movieId;
    private String movieTitle;
    private Integer companiesCreated;
    private Integer companiesReused;
    private Integer countriesCreated;
    private Integer languagesCreated;
    private Integer keywordsCreated;
    private Boolean collectionCreated;
    private String message;
}
