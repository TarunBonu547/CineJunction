package com.cinejunction.tmdb.service;

import com.cinejunction.tmdb.dto.CreditsResponseDto;
import com.cinejunction.tmdb.dto.KeywordDto;
import com.cinejunction.tmdb.dto.MovieDetailsDto;

import java.util.List;

public interface TMDbService {

    MovieDetailsDto getMovie(Long id);

    CreditsResponseDto getMovieCredits(Long movieId);

    List<KeywordDto> getMovieKeywords(Long movieId);
}
