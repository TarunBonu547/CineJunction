package com.cinejunction.rating.repository;

import com.cinejunction.entity.User;
import com.cinejunction.movie.entity.Movie;
import com.cinejunction.rating.entity.MovieRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface MovieRatingRepository extends JpaRepository<MovieRating, Long> {

    boolean existsByMovieAndUser(Movie movie, User user);

    List<MovieRating> findByMovie(Movie movie);

    List<MovieRating> findByUser(User user);

    Page<MovieRating> findByMovieId(Long movieId, Pageable pageable);

    List<MovieRating> findByMovieId(Long movieId);

    Page<MovieRating> findByUserId(Long userId, Pageable pageable);

    List<MovieRating> findByUserId(Long userId);

    Optional<MovieRating> findByMovieIdAndUserId(Long movieId, Long userId);

    void deleteByMovieIdAndUserId(Long movieId, Long userId);

    long countByMovie(Movie movie);

    @Query("SELECT AVG(mr.rating) FROM MovieRating mr WHERE mr.movie.id = :movieId")
    Double findAverageRatingByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT mr.rating, COUNT(mr) FROM MovieRating mr WHERE mr.movie.id = :movieId GROUP BY mr.rating ORDER BY mr.rating")
    List<Object[]> findRatingDistributionByMovieId(@Param("movieId") Long movieId);
}
