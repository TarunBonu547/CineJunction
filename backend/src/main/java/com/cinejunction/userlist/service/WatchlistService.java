package com.cinejunction.userlist.service;

import com.cinejunction.movie.dto.MovieSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface WatchlistService {
    void addToWatchlist(Long userId, Long movieId);
    void removeFromWatchlist(Long userId, Long movieId);
    Page<MovieSummaryResponse> getWatchlist(Long userId, Pageable pageable);
    void updateWatchedStatus(Long userId, Long movieId, boolean watched);
    Map<String, Object> getWatchlistStatus(Long userId, Long movieId);
}
