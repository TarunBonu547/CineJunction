package com.cinejunction.movie.repository;

import com.cinejunction.movie.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Movie} entities.
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {

    /**
     * Finds a movie by its title, ignoring case.
     *
     * @param title the movie title
     * @return an {@link Optional} containing the movie, or empty if not found
     */
    Optional<Movie> findByTitleIgnoreCase(String title);

    /**
     * Finds all movies by their title, ignoring case.
     *
     * @param title the movie title
     * @return a {@link List} of matching movies
     */
    List<Movie> findAllByTitleIgnoreCase(String title);

    /**
     * Checks whether a movie with the given title exists, ignoring case.
     *
     * @param title the movie title
     * @return {@code true} if a movie with the title exists, {@code false} otherwise
     */
    boolean existsByTitleIgnoreCase(String title);

    /**
     * Finds movies whose title contains the given keyword, ignoring case.
     *
     * @param keyword  the search keyword
     * @param pageable pagination information
     * @return a {@link Page} of matching movies
     */
    Page<Movie> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}
