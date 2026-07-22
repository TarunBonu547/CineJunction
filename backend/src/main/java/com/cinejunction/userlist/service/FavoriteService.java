package com.cinejunction.userlist.service;

import com.cinejunction.movie.dto.MovieSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface FavoriteService {
    void addFavorite(Long userId, Long movieId);
    void removeFavorite(Long userId, Long movieId);
    Page<MovieSummaryResponse> getFavorites(Long userId, Pageable pageable);
    boolean isFavorited(Long userId, Long movieId);
}
