package com.cinejunction.genre.repository;

import com.cinejunction.genre.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Genre} entities.
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    /**
     * Finds a genre by its name, ignoring case.
     *
     * @param name the genre name
     * @return an {@link Optional} containing the genre, or empty if not found
     */
    Optional<Genre> findByNameIgnoreCase(String name);

    /**
     * Checks whether a genre with the given name exists, ignoring case.
     *
     * @param name the genre name
     * @return {@code true} if a genre with the name exists, {@code false} otherwise
     */
    boolean existsByNameIgnoreCase(String name);
}
