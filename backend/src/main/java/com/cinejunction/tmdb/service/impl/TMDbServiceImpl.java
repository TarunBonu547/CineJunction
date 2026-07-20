package com.cinejunction.tmdb.service.impl;

import com.cinejunction.tmdb.client.TMDbClient;
import com.cinejunction.tmdb.dto.CreditsResponseDto;
import com.cinejunction.tmdb.dto.KeywordDto;
import com.cinejunction.tmdb.dto.MovieDetailsDto;
import com.cinejunction.tmdb.service.TMDbService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TMDbServiceImpl implements TMDbService {

    private final TMDbClient tmDbClient;

    @Override
    public MovieDetailsDto getMovie(Long id) {
        return tmDbClient.getMovieById(id);
    }

    @Override
    public CreditsResponseDto getMovieCredits(Long movieId) {
        return tmDbClient.getMovieCredits(movieId);
    }

    @Override
    public List<KeywordDto> getMovieKeywords(Long movieId) {
        return tmDbClient.getMovieKeywords(movieId);
    }
}
