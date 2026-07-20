package com.cinejunction.tmdb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDetailsDto {

    private Long id;
    private String title;
    private String overview;
    private String release_date;
    private Integer runtime;
    private String poster_path;
    private String backdrop_path;
    private Double vote_average;
    private Integer vote_count;
    private Double popularity;
    private List<GenreDto> genres;
    private Boolean adult;
    private Long budget;
    private Long revenue;
    private String original_language;
    private List<ProductionCompanyDto> production_companies;
    private List<ProductionCountryDto> production_countries;
    private List<SpokenLanguageDto> spoken_languages;
    private CollectionDto belongsToCollection;
}
