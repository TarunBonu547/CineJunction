package com.cinejunction.userlist.service.impl;

import com.cinejunction.entity.User;
import com.cinejunction.movie.dto.MovieSummaryResponse;
import com.cinejunction.movie.entity.Movie;
import com.cinejunction.movie.mapper.MovieMapper;
import com.cinejunction.movie.repository.MovieRepository;
import com.cinejunction.userlist.entity.Watchlist;
import com.cinejunction.userlist.exception.WatchlistAlreadyExistsException;
import com.cinejunction.userlist.exception.WatchlistNotFoundException;
import com.cinejunction.userlist.repository.WatchlistRepository;
import com.cinejunction.userlist.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WatchlistServiceImpl implements WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    @Override
    @Transactional
    public void addToWatchlist(Long userId, Long movieId) {
        if (watchlistRepository.existsByMovieIdAndUserId(movieId, userId)) {
            throw new WatchlistAlreadyExistsException("Movie already in watchlist");
        }
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new WatchlistNotFoundException("Movie not found with id: " + movieId));
        User user = new User();
        user.setId(userId);
        Watchlist watchlist = Watchlist.builder()
                .movie(movie)
                .user(user)
                .watched(false)
                .build();
        watchlistRepository.save(watchlist);
    }

    @Override
    @Transactional
    public void removeFromWatchlist(Long userId, Long movieId) {
        if (!watchlistRepository.existsByMovieIdAndUserId(movieId, userId)) {
            throw new WatchlistNotFoundException("Watchlist entry not found for movie id: " + movieId);
        }
        watchlistRepository.deleteByMovieIdAndUserId(movieId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieSummaryResponse> getWatchlist(Long userId, Pageable pageable) {
        return watchlistRepository.findByUserId(userId, pageable)
                .map(watchlist -> movieMapper.toSummary(watchlist.getMovie()));
    }

    @Override
    @Transactional
    public void updateWatchedStatus(Long userId, Long movieId, boolean watched) {
        Watchlist watchlist = watchlistRepository.findByMovieIdAndUserId(movieId, userId)
                .orElseThrow(() -> new WatchlistNotFoundException("Watchlist entry not found for movie id: " + movieId));
        watchlist.setWatched(watched);
        watchlist.setWatchedDate(watched ? Instant.now() : null);
        watchlistRepository.save(watchlist);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getWatchlistStatus(Long userId, Long movieId) {
        if (!watchlistRepository.existsByMovieIdAndUserId(movieId, userId)) {
            throw new WatchlistNotFoundException("Watchlist entry not found for movie id: " + movieId);
        }
        Watchlist watchlist = watchlistRepository.findByMovieIdAndUserId(movieId, userId)
                .orElseThrow(() -> new WatchlistNotFoundException("Watchlist entry not found for movie id: " + movieId));
        Map<String, Object> status = new HashMap<>();
        status.put("inWatchlist", true);
        status.put("watched", watchlist.isWatched());
        status.put("watchedDate", watchlist.getWatchedDate());
        return status;
    }
}
