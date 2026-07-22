package com.cinejunction.userlist.repository;

import com.cinejunction.entity.User;
import com.cinejunction.movie.entity.Movie;
import com.cinejunction.userlist.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByMovieAndUser(Movie movie, User user);
    boolean existsByMovieIdAndUserId(Long movieId, Long userId);
    Optional<Favorite> findByMovieIdAndUserId(Long movieId, Long userId);
    Page<Favorite> findByUserId(Long userId, Pageable pageable);
    long countByUserId(Long userId);
    void deleteByMovieIdAndUserId(Long movieId, Long userId);
}
