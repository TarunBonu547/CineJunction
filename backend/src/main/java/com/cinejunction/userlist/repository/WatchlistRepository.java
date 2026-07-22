package com.cinejunction.userlist.repository;

import com.cinejunction.entity.User;
import com.cinejunction.movie.entity.Movie;
import com.cinejunction.userlist.entity.Watchlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    boolean existsByMovieAndUser(Movie movie, User user);
    boolean existsByMovieIdAndUserId(Long movieId, Long userId);
    Optional<Watchlist> findByMovieIdAndUserId(Long movieId, Long userId);
    Page<Watchlist> findByUserId(Long userId, Pageable pageable);
    long countByUserId(Long userId);
    void deleteByMovieIdAndUserId(Long movieId, Long userId);
}
