package com.cinejunction.review.repository;

import com.cinejunction.entity.User;
import com.cinejunction.movie.entity.Movie;
import com.cinejunction.review.entity.MovieReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieReviewRepository extends JpaRepository<MovieReview, Long> {

    boolean existsByMovieAndUser(Movie movie, User user);

    List<MovieReview> findByMovie(Movie movie);

    List<MovieReview> findByUser(User user);

    Page<MovieReview> findByMovieId(Long movieId, Pageable pageable);

    List<MovieReview> findByMovieId(Long movieId);

    Page<MovieReview> findByUserId(Long userId, Pageable pageable);

    List<MovieReview> findByUserId(Long userId);

    Optional<MovieReview> findByMovieIdAndUserId(Long movieId, Long userId);

    void deleteByMovieIdAndUserId(Long movieId, Long userId);

    long countByMovie(Movie movie);

    List<MovieReview> findByMovieIdAndContainsSpoilersTrue(Long movieId);

    @Query("SELECT mr FROM MovieReview mr WHERE LOWER(mr.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(mr.reviewText) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<MovieReview> searchReviews(@Param("keyword") String keyword, Pageable pageable);
}
