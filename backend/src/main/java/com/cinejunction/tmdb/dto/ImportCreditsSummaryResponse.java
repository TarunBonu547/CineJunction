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
public class ImportCreditsSummaryResponse {

    private Long movieId;
    private String movieTitle;
    private Integer personsCreated;
    private Integer personsReused;
    private Integer relationshipsCreated;
    private Integer relationshipsSkipped;
    private String message;
}
