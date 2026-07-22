package com.cinejunction.userlist.service.impl;

import com.cinejunction.entity.User;
import com.cinejunction.movie.dto.MovieSummaryResponse;
import com.cinejunction.movie.entity.Movie;
import com.cinejunction.movie.mapper.MovieMapper;
import com.cinejunction.movie.repository.MovieRepository;
import com.cinejunction.userlist.entity.Favorite;
import com.cinejunction.userlist.exception.FavoriteAlreadyExistsException;
import com.cinejunction.userlist.exception.FavoriteNotFoundException;
import com.cinejunction.userlist.repository.FavoriteRepository;
import com.cinejunction.userlist.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    @Override
    @Transactional
    public void addFavorite(Long userId, Long movieId) {
        if (favoriteRepository.existsByMovieIdAndUserId(movieId, userId)) {
            throw new FavoriteAlreadyExistsException("Movie already in favorites");
        }
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new FavoriteNotFoundException("Movie not found with id: " + movieId));
        User user = new User();
        user.setId(userId);
        Favorite favorite = Favorite.builder()
                .movie(movie)
                .user(user)
                .build();
        favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long movieId) {
        if (!favoriteRepository.existsByMovieIdAndUserId(movieId, userId)) {
            throw new FavoriteNotFoundException("Favorite not found for movie id: " + movieId);
        }
        favoriteRepository.deleteByMovieIdAndUserId(movieId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieSummaryResponse> getFavorites(Long userId, Pageable pageable) {
        return favoriteRepository.findByUserId(userId, pageable)
                .map(favorite -> movieMapper.toSummary(favorite.getMovie()));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorited(Long userId, Long movieId) {
        return favoriteRepository.existsByMovieIdAndUserId(movieId, userId);
    }
}
